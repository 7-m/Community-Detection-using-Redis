package org.mfd.communtiydetection.clientside;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.mfd.communtiydetection.Communicator;
import org.mfd.communtiydetection.Message;
import org.mfd.communtiydetection.Node;
import org.mfd.communtiydetection.NodeCodec;
import org.mfd.communtiydetection.Message.MessageType;
import org.mfd.communtiydetection.NodeHandle;
import org.mfd.communtiydetection.RedisHandle;
import org.mfd.communtiydetection.Utils;

import com.lambdaworks.redis.cluster.ClusterClientOptions;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;

public class ClientApp {
	static BlockingQueue<int[]> nodesQueue = new LinkedBlockingQueue<>();
	static NodeHandle nh;
	static WorkerManager wm;
	static Communicator comm;

	public static void main(String[] args) {

		/* 
		 * contact the manager and set up the server
		 */

		//options initialized with defaults
		String host = null;
		int port = 52000;
		String redisURI = "redis://localhost:7000/0";

		//arguments handler
		for (int i = 0; i < args.length; i++) {

			try {
				switch (args[i]) {
				case "--host":
					host = args[++i];
					break;
				case "--port":
					port = Integer.valueOf(args[++i]);
					break;
				case "--redisURI":
					redisURI = args[++i];
					break;
				case "--help":
					System.out.println(
							"usage : java -jar ClientApp.jar [--host HOST] [--port PORT] [--redisURI REDISURI].\nDefaults are port=52000 and host=127.0.0.1 and redisURI = redis://localhost:7000/0");
					System.exit(1);
				default:
					System.out.println(
							"usage : java -jar ClientApp.jar [--host HOST] [--port PORT] [--redisURI REDISURI].\nDefaults are port=52000 and host=127.0.0.1 and redisURI = redis://localhost:7000/0");
					System.exit(1);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e2) {
				System.err.println(args[--i] + " requires an argument");
			}

		}
		System.out.println("Host: " + host + "\nport : " + port + "\nredisUri : " + redisURI);
		initRedisHandle(redisURI);
		wm = new WorkerManager(nh, new NodeSupplyControlImpl());

		Socket managerSock;
		try {
			Utils.logI("Connecting to " + port);
			managerSock = new Socket(host, port);
			Utils.logI("connected to " + managerSock);
			comm = new Communicator(managerSock, new MessageProcessorImpl());

			comm.call();//run the communicator on the main thread, make sure nothing else runs which might cause the thread to block

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static void initRedisHandle(String redisURI) {

		RedisClusterClient cli = RedisClusterClient.create(redisURI);
		cli.setOptions(ClusterClientOptions.builder().validateClusterNodeMembership(false).build());
		StatefulRedisClusterConnection<Integer, Node> conn = cli.connect(new NodeCodec());
		nh = new RedisHandle(conn.sync());

	}

	static class MessageProcessorImpl implements Communicator.MessageProcessor {

		@Override
		public void process(Message m) {
			MessageType type = m.getMessageType();

			switch (type) {

			case NODESET:
				nodesQueue.add((int[]) m.getData()[0]);
				//assert nodes != null;

				break;
			case DETECTION_START:
				/*run the detection work manager in another thread to prevent the main thread from going into a dead lock in which
				 * the main thread blocks trying to get nodes and hence cannot read  messages which actually contain the nodes*/
				new Thread(() -> {
					try {
						int nodesUpdated = wm.runFor(nodesQueue.take());
						comm.send(new Message(MessageType.DETECTION_END, nodesUpdated));
					} catch (IOException | InterruptedException e) {

						e.printStackTrace();
					}

				}).start();
				break;
			default:
				assert false : "God help you my friend";

			}

		}
	}

	static class NodeSupplyControlImpl implements WorkerManager.NodeSupplyControl {

		static Message START = new Message(MessageType.START_SUPPLY);
		static Message STOP = new Message(MessageType.STOP_SUPPLY);

		@Override
		public void startSupply() {
			try {
				comm.send(START);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void stopSupply() {
			try {
				comm.send(STOP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
