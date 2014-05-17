package com.texa.odblogbt;

import com.autoalert.Hex;

public class CommandGetParameterValue extends Command {

	int parameterId;

	public CommandGetParameterValue(int parameterId) {
		this.parameterId = parameterId;  
	}
	
	@Override
	public byte getCommandId() {
		// TODO Auto-generated method stub
		return 0x2F;
	}

	@Override
	public byte[] getMessageBytes() {
		// TODO Auto-generated method stub
		return new byte[] { 0x00, 0x00, (byte)(parameterId >> 8), (byte)( parameterId) };
	}
	
	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse res = new CommandResponse();
		
		byte[] valueBytes = new byte[response.length - 5];
		for (int i = 0; i < valueBytes.length - 5; i++) {
			valueBytes[i] = response[i+5];
		}
		
		res.put("value", Hex.bytesToHex(valueBytes));
		res.put("commandStatus", String.valueOf(response[0]));
		res.put("mode", String.valueOf(response[1]));
		res.put("parameterId", String.valueOf(response[2] * 256 + response[3]));
		
		return res;
	}
}
