package com.texa.odblogbt;

public class CommandPing extends Command {

	@Override
	public byte getCommandId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[0];
	}

	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse cmdResponse =  new CommandResponse();
		cmdResponse.put("value", String.valueOf(response[0]));
		
		return cmdResponse;
	}

}
