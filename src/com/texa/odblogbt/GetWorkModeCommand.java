package com.texa.odblogbt;

public class GetWorkModeCommand extends Command {

	@Override
	protected byte getCommandId() {
		// TODO Auto-generated method stub
		return 0x11;
	}

	@Override
	protected byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[0];
	}

	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse r = new CommandResponse();
		
		r.put("WorkMode", String.valueOf(response[2]));
		
		return r;
	}

}
