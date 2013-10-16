package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.util.concurrent.Callable;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public abstract class BluetoothServerHandler implements Callable<Void>{

	public static final String BROADCAST_CLIENTS_CONNECTION_UPDATE = "de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler.clientsconnectedupdate"; 
	
	protected final BluetoothServerService service;
	protected final BluetoothSocket socket;
	
	private static volatile int clientsConnected=0;
	
	public static int getClientsConnected(){
		return clientsConnected;
	}
	
	private void clientConnected(boolean disconnect){
		if(disconnect){
			clientsConnected--;
		}else{
			clientsConnected++;
		}
		
		Intent intent = new Intent();
		intent.setAction(BROADCAST_CLIENTS_CONNECTION_UPDATE);
		service.sendBroadcast(intent);
	}
	
	public BluetoothServerHandler(BluetoothServerService service,BluetoothSocket socket) {
		this.service=service;
		this.socket = socket;
		
		clientConnected(false);
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
		clientConnected(true);
		service.unloadHandler(this);
	}
}
