package de.uniulm.bagception.bluetoothserver.service.impl;

import java.io.IOException;

import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothEchoHandler extends BluetoothServerHandler{

	public BluetoothEchoHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		super(service, socket);
	}

	@Override
	protected void recv(char c) {
		Log.d("RECV",c+"");
		try {
			socket.getOutputStream().write((int) c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
