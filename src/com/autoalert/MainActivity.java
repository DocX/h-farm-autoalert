package com.autoalert;

import java.io.IOException;
import java.util.UUID;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main2,
					container, false);
			return rootView;
		}
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
    
    private byte[] pingCommand() {
    	return createPacket((byte)0x00, (byte)0x00, new byte[] {0x00});
    }
    
    public void connect(View view) throws IOException {
        BluetoothSocket tmp = null;
        
        BluetoothDevice device = findDevice();
        
        tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
        
        tmp.getOutputStream().write(pingCommand());
        
        byte[] buffer = new byte[8];
        tmp.getInputStream().read(buffer);
        
        ((TextView)findViewById(R.id.textView1)).setText( Hex.bytesToHex(buffer));        
    }

    private BluetoothDevice findDevice() {
    	BluetoothAdapter adapter = null;
    	
    	for (BluetoothDevice device : adapter.getBondedDevices()) {
    		if(device.getName() == "TO9DT000069") {
    			return device;
    		}
		}
    	
    	return null;
	}


}
