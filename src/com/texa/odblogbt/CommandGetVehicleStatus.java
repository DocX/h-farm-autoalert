package com.texa.odblogbt;

import android.renderscript.Sampler.Value;

public class CommandGetVehicleStatus extends Command {
	
	@Override
	protected byte getCommandId() {
		// TODO Auto-generated method stub
		return (byte)0xE6;
	}

	@Override
	protected byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[] {0x00,0x01};
	}

	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse r = new CommandResponse();
		
		r.put("Status", String.valueOf(response[3] & 0xFF));
		
		return r;
	}

}
