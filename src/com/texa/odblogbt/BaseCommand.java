package com.texa.odblogbt;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BaseCommand {

	private BluetoothSocket socket;
	
	public BaseCommand(BluetoothSocket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}
	
	
	
	private int crc(byte[] bytes) {
    	int crc = 0x00BD;          // initial value
        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

        // byte[] testBytes = "123456789".getBytes("ASCII");

        for (int j = 2; j < bytes.length - 4; j++) {
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
    
    public byte[] createPacket(byte repeatByte, byte cmdId, byte[] message) {
    	byte deviceAddress = (byte) 0x92;
    	byte[] packet = new byte[message.length + 2 /* sop */ + 1 /* dev addr */ 
    	                  + 1 /* length */ + 1 /* cmd id */ 
    	                  + 2 /* crc*/];
    	packet[0] = 0x02;
    	packet[1] = 0x02;
    	
    	/* dev address */
    	packet[2] = deviceAddress;
    	
    	// length
    	packet[3] = (byte) message.length;
    	
    	// msg
    	for (int i = 0; i < message.length; i++) {
			packet[i+4] = message[i];
		}
    	
    	// crc
    	int crc = crc(packet);
    	
    	// TODO little or big endian
    	packet[packet.length - 2] = (byte)(crc >> 8);
    	packet[packet.length - 1] = (byte)(crc);
    	
    	return packet;
    }
    
    /**
     * return command status of ping
     * @return
     * @throws IOException
     */
    public byte pingCommand() throws IOException {
    	byte[] response = sendPacket((byte)0x00, (byte)0x00, new byte[] {0x00});
    	
    	return response[0];
    	
    }
    
    public byte[] getParameterValue(int parameterId) throws IOException {
    	byte[] response = sendPacket((byte) 0x2F, (byte)0x00,
    			new byte[] { 0x00, 0x00, (byte)(parameterId >> 8), (byte)( parameterId) });
    	
    	byte[] value = new byte[response.length - 7];
    	for (int i = 0; i < response.length - 7; i++) {
			value[i] = response[i+5];
		}
    	return value;
    }
    
    
    protected byte[] sendPacket(byte repeatByte, byte cmdId, byte[] message) throws IOException {
    	return sendCommand(
    			createPacket(repeatByte, cmdId, message)
    			);
    }
    
    protected byte[] sendCommand(byte[] command) throws IOException {
    	BluetoothSocket socket = null;
                
        socket.getOutputStream().write(command);
        
        // read the header first to construct buffer
        byte[] header = new byte[4];
        socket.getInputStream().read(header);
        
        byte length = header[3];
        
        byte[] buffer = new byte[length+2];
        socket.getInputStream().read(buffer);
        
        return buffer;
    }
 }
