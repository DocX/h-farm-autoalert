package com.autoalert;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.texa.odblogbt.Command;
import com.texa.odblogbt.CommandGetParameterValue;
import com.texa.odblogbt.CommandGetVehicleStatus;
import com.texa.odblogbt.CommandPing;
import com.texa.odblogbt.CommandResponse;
import com.texa.odblogbt.Connection;
import com.texa.odblogbt.GetWorkModeCommand;
import com.texa.odblogbt.SetWorkModeCommand;
import com.texa.odblogbt.StartAcquisition;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import android.provider.Telephony.TextBasedSmsColumns;

public class MainActivity extends ActionBarActivity {

    private BluetoothSocket socket = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	public void startCollectionInBackground(View v) {
	    new Thread(new Runnable() {
	        public void run() {
	        	for(int i = 0; i < 10; i++) {
	        		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		Log.d("Testing", "another one");
	        	}
	        }
	    }).start();
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
		if (socket == null) {
			
			BluetoothDevice device = null;
	        
	        device = findDevice();
	        

			//socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
		    	
        
	        try {
	        	socket.connect();
	        } catch(Exception e) {
	        	Log.d("Exception in getSocket", e.toString());
	        	throw e;
	        }
		}
		
        return socket;
	}
    
    public void getCarValue(View view) throws Exception {

        View edit = findViewById(R.id.editText1);
        String paramIdHex = ((EditText)edit).getText().toString();
        int paramId = Integer.parseInt(paramIdHex, 16);
        Log.d("ParamID", String.valueOf(paramId));

        sendCommand(new CommandGetParameterValue(paramId));
    }

	private void sendCommand(Command getParamCommand) {
		
		try {
			Connection comm = new Connection(getSocket());
			
	        CommandResponse response = getParamCommand.send(comm, 
	        		((CheckBox)findViewById(R.id.checkBox1)).isChecked()
	        		);
	        
	        showResponse(response);
        }
        catch (Exception e)
        {
            ((TextView)findViewById(R.id.textView1)).setText( "ERROR " + e.toString() );                	
        }
	}

    protected void showResponse(CommandResponse response) {
        TextView text = ((TextView)findViewById(R.id.textView1));
        text.setText( "" );
        for (Entry<String,String> value : response.entrySet()) {
			text.append(value.getKey());
			text.append(":");
			text.append(value.getValue());
        	text.append("\n");
		}

    }
    
    public void ping(View view) throws Exception {
        sendCommand(new CommandPing());
    }
    
    
    public void vehicleStatus(View view) throws Exception {
        sendCommand(new CommandGetVehicleStatus());
    }
    

    public void getWorkMode(View view) throws Exception {
        sendCommand( new GetWorkModeCommand());
    }
    
    public void setWorkMode(View view) throws Exception {
        sendCommand( new SetWorkModeCommand(SetWorkModeCommand.CONNECTED));
    }
    public void setWorkModeTunning(View view) throws Exception {
        sendCommand( new SetWorkModeCommand(SetWorkModeCommand.TUNNING));
    }
    public void setWorkModeManual(View view) throws Exception {
        sendCommand(new SetWorkModeCommand(SetWorkModeCommand.MANUAL_SELF_STANDING));
    }
    
    public void startAcquisition(View view) throws Exception {
        sendCommand( new StartAcquisition());
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
