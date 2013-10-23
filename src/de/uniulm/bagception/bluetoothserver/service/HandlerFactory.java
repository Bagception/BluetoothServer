package de.uniulm.bagception.bluetoothserver.service;

import android.bluetooth.BluetoothSocket;


public interface HandlerFactory {

	public BluetoothServerHandler createHandler(BluetoothServerService service,
			BluetoothSocket socket);
}
