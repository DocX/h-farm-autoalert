package com.texa.odblogbt;

public class StartAcquisition extends Command {

	@Override
	protected byte getCommandId() {
		// TODO Auto-generated method stub
		return 0x25;
	}

	@Override
	protected byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[] {0x00};
	}

	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse cmdResponse =  new CommandResponse();
		cmdResponse.put("Status", String.valueOf(response[1]));
		
		return cmdResponse;
	}

}
