package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.util.concurrent.Callable;

import de.uniulm.bagception.bluetooth.BagceptionBTServiceInterface;
import de.uniulm.bagception.broadcastconstants.BagceptionBroadcastContants;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public abstract class BluetoothServerHandler implements Callable<Void> {


	protected final BluetoothServerService service;
	protected final BluetoothSocket socket;

	public BluetoothServerHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		this.service = service;
		this.socket = socket;

	}

	@Override
	public Void call() {
		boolean running=true;
		Log.d("Bluetooth", "BT handler active");
		int inp;
		byte[] buffer = new byte[1024];
		try {
			while (running) {

				inp = socket.getInputStream().read(buffer);
				if (inp == -1)
					break;
				recv(new String(buffer, 0, inp));

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Log.d("Bluetooth", "BT handler out of loop");
			running=false;
			close();
			

		}

		return null;

	}

	protected abstract void recv(String c);

	public synchronized void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		service.unloadHandler(this);
	}

	public void send(byte[] bytes) {
		try {
			socket.getOutputStream().write(bytes);
		} catch (IOException e) {
			close();
		}
	}
}
