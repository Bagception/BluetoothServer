package de.uniulm.bagception.bluetoothserver.service.impl;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import de.uniulm.bagception.bluetooth.protocol.messageLayer.PayloadContentLengthProtocol;
import de.uniulm.bagception.bluetooth.protocol.messageLayer.PayloadContentLengthProtocolCallback;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public  class PayloadContentLengthProtocolHandler extends BluetoothServerHandler implements PayloadContentLengthProtocolCallback{
	private final PayloadContentLengthProtocol pclp;

	public PayloadContentLengthProtocolHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		super(service, socket);
		pclp = new PayloadContentLengthProtocol(this);
	}

	@Override
	protected synchronized void recv(String c) {
		pclp.in(c);
		
	}

	
	public void send(String message){
		String pmessage=pclp.out(message);
		Log.d("PayloadContentLengthProtoocolHandler",pmessage);
		this.send(pmessage.getBytes());
	}
	
	public  void onMessageRecv(String message){
	}

}
