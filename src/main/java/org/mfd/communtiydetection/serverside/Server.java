package org.mfd.communtiydetection.serverside;
/**
 * Accepts request from clients and puts their respective communicators in a list for
 * future work allocation.
 * @author mfd
 *
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import org.mfd.communtiydetection.Communicator;
import org.mfd.communtiydetection.Communicator.MessageProcessor;

public class Server implements Runnable {

	List<Communicator> clientCommunicators;
	MessageProcessor msp;
	int port;

	public Server(List<Communicator> clientCommunicators, MessageProcessor msp, int port) {
		this.clientCommunicators = clientCommunicators;
		this.msp = msp;
		this.port = port;
	}

	@Override
	public void run() {
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(5000);//let it block for 5 seconds, its a decent time
			System.out.println("Server.java : listening for clients");
			while (!Thread.interrupted()) {
				try {
					
					Socket accept = server.accept();
					clientCommunicators.add(new Communicator(accept, msp));
					System.out.println("added client : "+accept);
				} catch (SocketTimeoutException e) {
					//do nothing

				}
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

}
