package de.uniulm.bagception.bluetoothserver.service.impl;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import de.uniulm.bagception.bluetooth.protocol.commandLayer.JSONCommandProtocol;
import de.uniulm.bagception.bluetooth.protocol.commandLayer.JSONCommandProtocolCallback;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public class JSONCommandProtocolHandler extends PayloadContentLengthProtocolHandler implements JSONCommandProtocolCallback{

	private final JSONCommandProtocol p;
	public JSONCommandProtocolHandler(BluetoothServerService service,
			BluetoothSocket socket) {
		super(service, socket);
		p = new JSONCommandProtocol(this);
	}

	@Override
	public void onMessageRecv(String message) {
		super.onMessageRecv(message);
		p.in(message);
		
	}

	
	@Override
	public void onCommandRecv(String cmd, String payload) {
		Log.d("bt","cmd: "+cmd+";; payload: "+payload);
		
	}
	
	
	public void send(String cmd,String pl) {
		super.send(p.out(cmd, pl));
	}
	
}
