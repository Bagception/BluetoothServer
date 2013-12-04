package de.uniulm.bagception.bluetoothserver.service.impl;

import android.bluetooth.BluetoothSocket;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;
import de.uniulm.bagception.protocol.message.PayloadContentLengthProtocol;
import de.uniulm.bagception.protocol.message.PayloadContentLengthProtocolCallback;

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
		this.send(pmessage.getBytes());
	}
	
	public  void onMessageRecv(String message){
	}

}
