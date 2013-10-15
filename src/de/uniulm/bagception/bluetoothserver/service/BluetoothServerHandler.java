package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.util.concurrent.Callable;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public abstract class BluetoothServerHandler implements Callable<Void>{

	protected final BluetoothServerService service;
	protected final BluetoothSocket socket;
	public BluetoothServerHandler(BluetoothServerService service,BluetoothSocket socket) {
		this.service=service;
		this.socket = socket;
	}

	@Override
	public Void call() throws Exception {
		Log.d("Bluetooth","BT handler active");
		int inp;
		
		while((inp = socket.getInputStream().read())!=-1){
			recv((char)inp);
		}
		
		Log.d("Bluetooth","BT handler out of loop");

		close();
		return null;
		
	}
	
	protected abstract void recv(char c);
	
	public void close(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		service.unloadHandler(this);
	}
}
