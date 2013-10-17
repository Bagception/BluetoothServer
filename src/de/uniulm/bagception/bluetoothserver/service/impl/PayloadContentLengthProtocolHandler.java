package de.uniulm.bagception.bluetoothserver.service.impl;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import de.uniulm.bagception.bluetooth.protocol.PayloadContentLengthProtocol;
import de.uniulm.bagception.bluetooth.protocol.PayloadContentLengthProtocolCallback;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public abstract class PayloadContentLengthProtocolHandler extends BluetoothServerHandler implements PayloadContentLengthProtocolCallback{
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

	
	
	public  void onMessageRecv(String message){
		Log.d("PROTOCOL","MSG: "+message);
	}

}
