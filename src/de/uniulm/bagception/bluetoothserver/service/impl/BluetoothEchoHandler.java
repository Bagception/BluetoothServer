package de.uniulm.bagception.bluetoothserver.service.impl;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public class BluetoothEchoHandler extends BluetoothServerHandler{

	public BluetoothEchoHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		super(service, socket);
	}

	@Override
	protected void recv(String c) {
		try {
			socket.getOutputStream().write(c.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
