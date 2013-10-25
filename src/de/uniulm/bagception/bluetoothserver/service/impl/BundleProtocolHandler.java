package de.uniulm.bagception.bluetoothserver.service.impl;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import de.uniulm.bagception.bluetooth.protocol.commandLayer.bundle.BundleProtocol;
import de.uniulm.bagception.bluetooth.protocol.commandLayer.bundle.BundleProtocolCallback;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public class BundleProtocolHandler extends PayloadContentLengthProtocolHandler implements BundleProtocolCallback{

	private final BundleProtocol protocol;
	public BundleProtocolHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		super(service, socket);
		protocol = new BundleProtocol(this);
		
	}
	
	@Override
	public void onMessageRecv(String message) {
		protocol.in(message);
	}

	@Override
	public void onBundleRecv(Bundle bundle) {
		service.sendToBoundHandler(this, bundle);
	}
	
	public void send(Bundle b){
		String toSend = protocol.out(b);
		this.send(toSend);
	}

}
