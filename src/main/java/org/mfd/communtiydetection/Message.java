package org.mfd.communtiydetection;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * {@link MessageType} determines the type of the message and hence the type
	 * of the data stored in it. {@link MessageType#DETECTION_END} notifies the
	 * completion of detection by a client. data[0] contains a boolean notifying
	 * wether nodes were updated or nood
	 * 
	 * {@link MessageType#NODESET} used supply the nodes in the
	 * beginning. data[0] contains int[] of  nodes.
	 * 
	 * {@link MessageType#DETECTION_START} used to command clients to begin
	 * detection on the nodes supplied.
	 * 
	 * @author mfd
	 *
	 */
	public enum MessageType {
		DETECTION_END, NODESET, DETECTION_START, START_SUPPLY, STOP_SUPPLY
	};

	MessageType messageType;
	Object[] data;

	public Message(MessageType messageType, Object... data) {
		super();
		this.messageType = messageType;
		this.data = data;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Object[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return messageType.toString();
	}

}
