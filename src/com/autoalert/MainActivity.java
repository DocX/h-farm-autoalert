package com.autoalert;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.texa.odblogbt.BaseCommand;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import android.provider.Telephony.TextBasedSmsColumns;

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
	
	private BluetoothSocket getSocket() throws Exception {
        BluetoothDevice device = null;
        
        device = findDevice();
        

        BluetoothSocket socket = null;
        socket = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
 

        return socket;
	}
    
    public void getCarValue(View view) throws Exception {

        BaseCommand comm = new BaseCommand(getSocket());
        
        View edit = findViewById(R.id.editText1);
        String paramIdHex = ((EditText)edit).getText().toString();
        int paramId = Integer.parseInt(paramIdHex, 16);
        Log.d("ParamID", String.valueOf(paramId));
        
        try {
	        byte[] value = comm.getParameterValue(paramId);
	        
            ((TextView)findViewById(R.id.textView1)).setText( Hex.bytesToHex(value));        
        }
        catch (Exception e)
        {
            ((TextView)findViewById(R.id.textView1)).setText( "error sending message getCarValue" );                	
        }
    }
    
    public void ping(View view) throws Exception {
        BaseCommand comm = new BaseCommand(getSocket());
                
        try {
	        byte value = comm.pingCommand();
	        Log.d("Ping", String.valueOf(value) );
        }
        catch (Exception e)
        {
        	Log.d("Ping Error", e.toString());                	
        }
    	
    }

    private BluetoothDevice findDevice() throws Exception {
    	BluetoothAdapter adapter = null;
    	
    	adapter = BluetoothAdapter.getDefaultAdapter();
    	Set<BluetoothDevice> devices = adapter.getBondedDevices();
    	
    	for (BluetoothDevice device : devices) {
    		if(device.getName().equals("TO9DT000069")) {
    			return device;
    		}
		}
    	
    	throw new Exception("Exception in findDevice");
	}


}
