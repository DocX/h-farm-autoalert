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
		return new byte[] { 0x00, (byte)(parameterId >> 8), (byte)( parameterId) };
	}
	
	@Override
	protected int extraLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected CommandResponse constructResponse(byte[] response) {
		// TODO Auto-generated method stub
		CommandResponse res = new CommandResponse();
		
		byte[] valueBytes = new byte[response.length - 7];
		for (int i = 0; i < response.length - 7; i++) {
			valueBytes[i] = response[i+5];
		}
		
		// 0 cmdid
		// 1 status
		// 2 mode - 00
		// 3-4 param id
		// 5-.. value
		
		res.put("valueHex", Hex.bytesToHex(valueBytes));
		int valueNum = 0;
		for (int i = 0; i < valueBytes.length; i++) {
			valueNum += (valueBytes[i] & 0xFF) << (i * 8);
		}
		res.put("valueNum", String.valueOf(valueNum));
		res.put("commandStatus", String.valueOf(response[1]));
		res.put("mode", String.valueOf(response[2]));
		res.put("parameterId", String.valueOf(response[3] * 256 + response[4]));
		
		return res;
	}
}
