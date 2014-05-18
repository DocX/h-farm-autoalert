package com.texa.odblogbt;

public class SetWorkModeCommand extends Command {

	public static byte NON_SELECTED = 0x00;
	public static byte CONNECTED = 0x02;
	public static byte MANUAL_SELF_STANDING = 0x03;
	public static byte TUNNING = 0x04;
	private byte mode;
	
	public SetWorkModeCommand(byte mode) {
		// TODO Auto-generated constructor stub
		this.mode = mode;
	}
	
	@Override
	protected byte getCommandId() {
		// TODO Auto-generated method stub
		return 0x10;
	}

	@Override
	protected byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[] {mode};
	}

	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse cmdResponse =  new CommandResponse();
		cmdResponse.put("Status", String.valueOf(response[1]));
		
		return cmdResponse;
	}

}
