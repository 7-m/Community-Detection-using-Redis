package org.mfd.communtiydetection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Communicator implements Callable<Object> {
	Socket socket;
	MessageProcessor msp;
	ObjectInputStream ois;// for reading messages
	ObjectOutputStream oos;//for writing messages

	public Communicator(Socket socket, MessageProcessor msp) throws IOException {

		this.socket = socket;
		this.msp = msp;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());

	}

	@Override
	public Object call() throws InterruptedException {

		try {
			while (!Thread.interrupted()) {

				Message m = (Message) ois.readObject();
				Utils.logI("Communicator.java : Recieved message " + m);
				msp.process(m);

			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		throw new InterruptedException();

	}

	public void send(Message m) throws IOException {
		//initOutputStream();
		oos.writeObject(m);
		Utils.logI("Communicator.java : sent message " + m.getMessageType());
		oos.flush();
	}

	/*private void initOutputStream() throws IOException {
		if (oos == null)
			oos = new ObjectOutputStream(socket.getOutputStream());
	
	}
	private void initInputStream() throws IOException {
		if (ois == null)
			ois = new ObjectInputStream(socket.getInputStream());
	
	}*/

	public interface MessageProcessor {
		void process(Message m);

	}

}
