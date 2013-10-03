package de.uniulm.bagception.bluetoothserver.service;

import java.util.concurrent.Callable;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothServerHandler implements Callable<Void>{

	public BluetoothServerHandler(BluetoothSocket socket) {
	
	}

	@Override
	public Void call() throws Exception {
		Log.d("Bluetooth","BT handler active");
		return null;
	}
}
