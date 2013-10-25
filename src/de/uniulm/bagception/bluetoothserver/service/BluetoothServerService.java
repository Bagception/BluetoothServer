package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import de.philipphock.android.lib.logging.LOG;
import de.philipphock.android.lib.services.messenger.MessengerService;
import de.uniulm.bagception.bluetooth.BagceptionBTServiceInterface;
import de.uniulm.bagception.bluetoothserver.service.impl.BundleProtocolHandler;

public class BluetoothServerService extends MessengerService implements Runnable, BagceptionBTServiceInterface {
	
	public static final int MESSAGE_TYPE_SENDMESSAGE=1;
	
	private Thread acceptThread;
	private boolean keepAlive = true;

    private final int executorCorePoolSize = 1;
    private final int executorMaxPoolSize = 10;
    private final int executorKeepAliveTime = 10;
    
    private final ThreadPoolExecutor executor;
    private BluetoothServerSocket currWaitingSocket;
	
	private ConcurrentHashMap<String, BluetoothServerHandler> handlermap;

	private final HandlerFactory handlerFactory;
    
	public static final int MESSAGE_HANDLER_RECV=0;
	

    
    public BluetoothServerService() {
    	this.handlerFactory = new HandlerFactory() {
			
			@Override
			public BluetoothServerHandler createHandler(BluetoothServerService service,
					BluetoothSocket socket) {
				
				return new BundleProtocolHandler(service,socket);
				
			}
		};
    	executor = new ThreadPoolExecutor(executorCorePoolSize, executorMaxPoolSize, executorKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));
	}
    

	/* ============ Service ============== */

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	/* ============ Runnable ============== */

	@Override
	public void run() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		try {
			currWaitingSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_SERVICE_NAME, UUID.fromString(BT_UUID));
			while (keepAlive){
				
				BluetoothSocket acceptedSocket = currWaitingSocket.accept();
				BluetoothServerHandler h = handlerFactory.createHandler(this,acceptedSocket);
				handlermap.put(h.toString(),h);
				executor.submit(h);
				
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		for (BluetoothServerHandler handler : handlermap.values()) {
		    handler.close();
		}
		handlermap.clear();
	}
	

	
	@Override
	protected void onFirstInit() {
		LOG.out(this,"Service init");

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
		    // Device does not support Bluetooth
			
			return;
		}
		
		if (!bluetoothAdapter.isEnabled()) {
			
		    return;
		}
		
		handlermap = new ConcurrentHashMap<String, BluetoothServerHandler>();

		acceptThread = new Thread(this);
		acceptThread.start();
	}

	
	
	@Override
	public void onDestroy() {
		keepAlive = false;
		try {
			currWaitingSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	
	
	void unloadHandler(BluetoothServerHandler btsh){
		handlermap.remove(btsh.toString());		
	}

	
	/**
	 * Sends a String to all Handler, 
	 * the handler will then send this to their remote devices
	 * @param s the string to send
	 */
	private void allHandlerSendToRemoteDevice(Bundle b){
		for (BluetoothServerHandler handler : handlermap.values()) {
			BundleProtocolHandler handlerCasted = ((BundleProtocolHandler)handler); 
		    handlerCasted.send(b);
		}
	}
	

	/**
	 * Sends messages to bound activities
	 * @param h the handler that sends the message
	 * @param b data
	 */
	public void sendToBoundHandler(BluetoothServerHandler h,Bundle b){
		
		Message m = Message.obtain(null, MESSAGE_HANDLER_RECV);
		m.setData(b);
		sendToClients(m);
	}
	

	@Override
	protected void handleMessage(Message m) {
		switch (m.what){
		case MESSAGE_TYPE_SENDMESSAGE:
			allHandlerSendToRemoteDevice(m.getData());
			break;
		}
	}
}
