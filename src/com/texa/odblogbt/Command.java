package com.texa.odblogbt;

import java.io.IOException;

public abstract class Command {

	protected abstract byte getCommandId();

	protected abstract byte[] getMessageBytes();
	
	protected abstract CommandResponse constructResponse(byte[] response);
	
	protected int extraLength() {
		return 0;
	}

	public CommandResponse send(Connection connection, boolean repeatedCommand) throws IOException {
		byte[] response = connection.sendPacket((byte) 0x00, getCommandId(), getMessageBytes(), extraLength(), repeatedCommand);
		if (response == null) {
			CommandResponse cr = new CommandResponse();
			cr.put("Error", "bad packet");
			return cr;
		}
		if (response[1] != 0) {
			CommandResponse cr = new CommandResponse();
			cr.put("Error", "Command not succesful - " + String.valueOf(response[1]));
			return cr;
		}
		return constructResponse(response);
	}

}
