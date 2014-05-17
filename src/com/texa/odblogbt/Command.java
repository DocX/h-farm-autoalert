package com.texa.odblogbt;

import java.io.IOException;

public abstract class Command {

	protected abstract byte getCommandId();

	protected abstract byte[] getMessageBytes();
	
	protected abstract CommandResponse constructResponse(byte[] response);

	public CommandResponse send(Connection connection) throws IOException {
		byte[] response = connection.sendPacket((byte) 0x00, getCommandId(), getMessageBytes());
		return constructResponse(response);
	}

}
