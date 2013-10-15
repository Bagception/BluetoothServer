package de.uniulm.bagception.bluetooth;

import android.bluetooth.BluetoothAdapter;

public class BluetoothHelper {
	
	public static boolean isEnabled(){
		return BluetoothAdapter.getDefaultAdapter().isEnabled();		
	}
}
