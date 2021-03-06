package com.texa.odblogbt;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.autoalert.Hex;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Connection {

	private BluetoothSocket socket;
	
	public Connection(BluetoothSocket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}
	
	private int crc(byte[] bytes) {
    	int crc = 0x00BD;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        // byte[] testBytes = "123456789".getBytes("ASCII");

        for (int j = 2; j < bytes.length - 2; j++) {
        	byte b = bytes[j];
        	
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
             }
        }

        crc &= 0xffff;
        
        return crc;
    }
	
	
    
    protected byte[] createPacket(byte repeatByte, byte cmdId, byte[] message) {
    	byte[] packet = new byte[
			message.length 
			+ 2 /* sop */ 
			+ 1 /* dev addr */ 
			+ 1 /* length */ 
			+ 1 /* cmd id */ 
			+ 1 /* repeat byte */
			+ 2 /* crc*/];
    	
    	/* packet start */
    	packet[0] = 0x02;
    	packet[1] = 0x02;
    	
    	/* dev address */
    	packet[2] = (byte)0x92;
    	
    	//  message length (cmd id and rep byte is part of the message)
    	packet[3] = (byte) (message.length + 2);
    	
    	packet[4] = (byte) cmdId;
    	packet[5] = (byte) repeatByte;
    	
    	// msg
    	for (int i = 0; i < message.length; i++) {
			packet[i+6] = message[i];
		}
    	
    	// crc
    	int crc = crc(packet);
    	
    	// TODO little or big endian
    	packet[packet.length - 2] = (byte)(crc >> 8);
    	packet[packet.length - 1] = (byte)(crc);
    	
    	return packet;
    }
   
    
    
    public byte[] sendPacket(byte repeatByte, byte cmdId, byte[] message, int extraLength, boolean repeatedCommand) throws IOException {
    	return sendCommand(
    			createPacket((byte)(repeatedCommand ? 0xFF : 0x00), cmdId, message), 
    			extraLength
    			);
    }
    
    
    protected byte[] sendCommand(byte[] command, int extraLength) throws IOException {
        
    	Log.d("PacketOut", Hex.bytesToHex(command));
    	
        socket.getOutputStream().write(command);

        InputStream inputStream = socket.getInputStream();

        byte secondByte = (byte) inputStream.read();
        byte firstByte  = (byte) inputStream.read();
        
        int count = 0;
        while (firstByte != 2 && secondByte != 2) {
        	secondByte = firstByte;
        	firstByte = (byte) inputStream.read();
        	count++;
        	
        	if (count > 10) {
        		return null;
        	}
        }
        
        Log.d("LoopCount", String.valueOf(count));
        
        // read the header first to construct buffer
        byte[] header = new byte[2];
        socket.getInputStream().read(header);
        Log.d("HeaderPacketIn", Hex.bytesToHex(header));
        
        int length = header[1] & 0xFF;

        byte[] buffer = new byte[length+2+extraLength];
        socket.getInputStream().read(buffer);
        Log.d("PacketIn", Hex.bytesToHex(buffer));

        //socket.getInputStream().skip(10);
        
        return buffer;
    }
 }
