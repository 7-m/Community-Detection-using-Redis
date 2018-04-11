package org.mfd.communtiydetection.serverside;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mfd.communtiydetection.Communicator;
import org.mfd.communtiydetection.Message;
import org.mfd.communtiydetection.Message.MessageType;
import org.mfd.communtiydetection.Node;
import org.mfd.communtiydetection.NodeCodec;
import org.mfd.communtiydetection.NodeHandle;
import org.mfd.communtiydetection.RedisHandle;
import org.mfd.communtiydetection.Utils;

import com.lambdaworks.redis.cluster.ClusterClientOptions;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;

public class ServerApp {

	static int nodes[];

	static List<Communicator> clientCommunicators = new Vector<>();;
	static List<Integer> nodesUpdatedCount = new Vector<>();
	static NodeSupplier ns;
	static Count iterationCount = new Count();
	static ExecutorService exs;

	static NodeHandle nh;
	

	

	static String outputPath;

	public static void main(String[] args) {
		/*
		 * create node[]
		 * shuffle the nodes and split them and deliver these nodes to the clients as initialization
		 * begin detection and keep goin until all clients report 0 updates
		 */

		//lets assume node[] is ready

		int serverPort = 52000;
		String redisURI = "redis://localhost:7000/0";
		String nodeFilePath = null;
		outputPath = null;

		for (int i = 0; i < args.length; i++) {

			try {
				switch (args[i]) {
				case "--file":
					nodeFilePath = args[++i];
					break;
				case "--port":
					serverPort = Integer.valueOf(args[++i]);
					break;
				case "--redisURI":
					redisURI = args[++i];
					break;
				case "--outputFile":
					outputPath = args[++i];
					break;
				case "--help":
					System.out.println(
							"usage : java -jar ServerApp.jar --file NODEFILE --outputFile OUTPUTFILE [--port PORT] [--redisURI REDISURI]\nIn case of no arguments, defaults to port = 52000 and redisURI = redis://localhost:7000/0 ");
					System.exit(0);

				default:
					System.out.println(
							"usage : java -jar ServerApp.jar --file NODEFILE --outputFile OUTPUTFILE [--port PORT] [--redisURI REDISURI]\nIn case of no arguments, defaults to port = 52000 and redisURI = redis://localhost:7000/0 ");
					System.exit(1);
				}

			} catch (IndexOutOfBoundsException e2) {
				System.err.println(args[--i] + " requires an argument");
				System.exit(1);
			}

		}
		if (nodeFilePath == null || outputPath == null) {
			System.err.println("NODEFILE and OUTPUTFILE required");
			System.exit(1);
		}
		if (!Utils.testOutputFile(outputPath)) {
			System.out.println(
					"Cant create output file " + outputPath + "\n Check if theres another file with the same name.");
			System.exit(1);
		}

		//******
		//setup connection to Redis cluster

		RedisClusterClient cli = RedisClusterClient.create(redisURI);
		cli.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
		StatefulRedisClusterConnection<Integer, Node> conn = cli.connect(new NodeCodec());
		nh = new RedisHandle(conn.sync());

		nodes = Utils.initNodes(nh, nodeFilePath);
		
		//******

		Server server = new Server(clientCommunicators, new MessageProcessorImpl(), serverPort);
		Thread serverThread = new Thread(server, "ServerThread");
		serverThread.start();
		Utils.logI("ServerApp.java: Started Server, waiting for clients to connect");

		System.out.println("Hit enter to stop waiting for clients and continue");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverThread.interrupt();
		if (clientCommunicators.isEmpty())
			throw new RuntimeException("No clients connected!");

		Utils.logI("Server stopped, connected clients are\n" + clientCommunicators + "\n Starting detection");
		Utils.logI("ServerApp.java: Graph order is " + nodes.length);

		//initialize the clients with the new nodes

		ns = new NodeSupplier(clientCommunicators, nodes, iterationCount);
		new Thread(ns, "NodeSupplierThread").start();
		//Future<?> f = null;

		Utils.logI("Done initializing clients");
		//now start the communicator threads
		exs = Executors.newFixedThreadPool(clientCommunicators.size());

		//if anything funny happens without any exceptions then check every Future returned by ExecutorService#Submit by calling Future#get(),
		//it'll start spitting them exceptions
		for (Communicator c : clientCommunicators)
			//f = 
			exs.submit(c);

		//run detection
		/*
		 * Heres how it works, the main thread starts the detection and dies, the communicator threads call
		 * runDetection() if required. just trace runDetection() and MessageImpl's DETECTON_END case to get an idea of whats goin on
		 */
		Utils.logI("ServerApp.java: Running detection");
		runDetection();

		//******for debugging uncomment the following block

		/*try {
			f.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		//once done stop all communicator threads

		//exs.shutdown();
	}

	private static void verifyDetection() {
		//first check if all clients are done with the detection
		if (nodesUpdatedCount.size() < clientCommunicators.size())
			return;//do nothing
		//for the detection to end the total no. of uodated nodes should be less than a certain preset factor
		int totalUpdates = 0;
		for (int result : nodesUpdatedCount) {
			totalUpdates += result;

		}
		if (totalUpdates > nodes.length * .02) {// the updated nodes should be less than some fraction well consider less than 3% of total nodes for success
			nodesUpdatedCount.clear();//reset the results
			runDetection();//go for another round				
			return;
		}

		System.out.println("Wrting communitites to file..");
		Utils.writeNodesToFile(nodes, nh, outputPath);

		System.exit(0);

	}

	private static void runDetection() {

		for (Communicator c : clientCommunicators)
			try {
				c.send(new Message(MessageType.DETECTION_START));
			} catch (IOException e) {

				e.printStackTrace();
			}
		Utils.logI("ServerApp.java : Running iteration no. : " + iterationCount.c++);

	}

	static class MessageProcessorImpl implements Communicator.MessageProcessor {

		@Override
		public void process(Message m) {
			switch (m.getMessageType()) {
			case DETECTION_END:
				nodesUpdatedCount.add((int) m.getData()[0]);
				Utils.logI("ServerApp.java : client replied with " + (int) m.getData()[0]);
				verifyDetection();
				break;

			case START_SUPPLY:
				ns.resume();
				break;
			case STOP_SUPPLY:
				//only one client is required to stop the supplying process
				ns.pause();

				break;

			default:
				assert false : "This code shouldnt have been executed";
				break;

			}

		}

	}

}
