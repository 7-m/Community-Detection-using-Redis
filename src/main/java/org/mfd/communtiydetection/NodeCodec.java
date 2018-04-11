package org.mfd.communtiydetection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.lambdaworks.redis.codec.RedisCodec;

public class NodeCodec implements RedisCodec<Integer, Node> {
	

	@Override
	public Integer decodeKey(ByteBuffer bytes) {

		return bytes.getInt();
	}

	@Override
	public Node decodeValue(ByteBuffer bytes) {

		try {
			byte[] array = new byte[bytes.remaining()];
			bytes.get(array);
			ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
			return (Node) is.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}

	}

	@Override
	public ByteBuffer encodeKey(Integer key) {

		return (ByteBuffer)ByteBuffer.allocate(4).putInt(key).flip();
	}

	@Override
	public ByteBuffer encodeValue(Node value) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ByteBuffer.wrap(baos.toByteArray());
	}

}
