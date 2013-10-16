package de.uniulm.bagception.bluetoothserver.bluetoothstate;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import de.philipphock.android.lib.BroadcastActor;

public class BluetoothStateActor extends
		BroadcastActor<BluetoothStateChangeReactor> {

	public BluetoothStateActor(BluetoothStateChangeReactor reactor) {
		super(reactor);
	}

	
	public void refireBluetoothCallbacks(){
		fireCallbacks(BluetoothAdapter.ACTION_STATE_CHANGED, BluetoothAdapter.getDefaultAdapter().getState());
		fireCallbacks(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED, BluetoothAdapter.getDefaultAdapter().getScanMode());
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
				BluetoothAdapter.ERROR);

		fireCallbacks(intent.getAction(),state);

	}

	
	private void fireCallbacks(String action, int state){
		Log.d("BT",action+" "+state);
		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

			switch (state) {
			case BluetoothAdapter.STATE_OFF:
				reactor.onBluetoothEnabledChanged(false);
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				reactor.onBluetoothTurningOff();
				break;
			case BluetoothAdapter.STATE_ON:
				reactor.onBluetoothEnabledChanged(true);
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				reactor.onBluetoothTurningOn();
				break;
			}

		} else if (action.equals(
				BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
			switch (state) {
			case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
				reactor.onBluetoothIsDiscoverable();
				break;
				
			case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
				reactor.onBluetoothIsConnectable();
				reactor.onBluetoothIsNotDiscoveralbe();
				break;
				
			case BluetoothAdapter.SCAN_MODE_NONE:
				reactor.onBluetoothIsNotConnectableAndNotDiscoveralbe();
				reactor.onBluetoothIsNotDiscoveralbe();
				break;
			}
		}
	}
	
	public void register(Context a) {
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		a.registerReceiver(this, filter);
		
	}

}
