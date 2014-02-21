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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import de.philipphock.android.lib.broadcast.blutooth.BluetoothStateActor;
import de.philipphock.android.lib.broadcast.blutooth.BluetoothStateChangeReactor;
import de.philipphock.android.lib.services.messenger.MessengerService;
import de.uniulm.bagception.bluetooth.BagceptionBTServiceInterface;
import de.uniulm.bagception.bluetoothserver.service.impl.BundleProtocolHandler;
import de.uniulm.bagception.bluetoothservermessengercommunication.MessengerConstants;
import de.uniulm.bagception.broadcastconstants.BagceptionBroadcastContants;

public class BluetoothServerService extends MessengerService implements Runnable, BagceptionBTServiceInterface, BluetoothStateChangeReactor {

	public static final int MESSAGE_TYPE_SENDMESSAGE=1;

	private Thread acceptThread;
	private boolean keepAlive = true;

    private final int executorCorePoolSize = 1;
    private final int executorMaxPoolSize = 10;
    private final int executorKeepAliveTime = 10;

    private final ThreadPoolExecutor executor;
    private BluetoothServerSocket currWaitingSocket;

	private ConcurrentHashMap<String, BluetoothServerHandler> handlermap;

    private BluetoothStateActor btState;
	private final HandlerFactory handlerFactory;




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
				sendHandlerCountNotification();
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
        btState = new BluetoothStateActor(this);
        btState.register(this);
		IntentFilter register = new IntentFilter(BagceptionBroadcastContants.BROADCAST_CLIENTS_CONNECTION_UPDATE_REQUEST);
		registerReceiver(statusRequestRecv, register);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
		    // Device does not support Bluetooth

			return;
		}

		if (!bluetoothAdapter.isEnabled()) {

		    return;
		}

		handlermap = new ConcurrentHashMap<String, BluetoothServerHandler>();
        startListening();

	}


	void startListening(){
        acceptThread = new Thread(this);
        acceptThread.start();
    }

    void stopListening(){
        keepAlive=false;
        try {
            currWaitingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void onDestroy() {
		for (BluetoothServerHandler handler : handlermap.values()) {
		    handler.close();
		}
        stopListening();

		unregisterReceiver(statusRequestRecv);
        btState.unregister(this);
		super.onDestroy();
	}



	void unloadHandler(BluetoothServerHandler btsh){
		handlermap.remove(btsh.toString());
		sendHandlerCountNotification();
	}


	/**
	 * Sends a String to all Handler,
	 * the handler will then send this to their remote devices
	 * @param b the string to send
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

		Message m = Message.obtain(null, MessengerConstants.MESSAGE_BUNDLE_MESSAGE);
		m.setData(b);
		sendToClients(m);
	}


	@Override
	protected void handleMessage(Message m) {
		switch (m.what){
		case MessengerConstants.MESSAGE_BUNDLE_MESSAGE:
			allHandlerSendToRemoteDevice(m.getData());
			break;

		}
	}

	private void sendHandlerCountNotification(){
		Intent intent = new Intent();
		intent.setAction(BagceptionBroadcastContants.BROADCAST_CLIENTS_CONNECTION_UPDATE);
		intent.putExtra(BagceptionBroadcastContants.BROADCAST_CLIENTS_CONNECTION_UPDATE,handlermap.size());
		sendBroadcast(intent);
	}

	BroadcastReceiver statusRequestRecv = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			sendHandlerCountNotification();
		}
	};

	

    // BluetoothStateChangeReactor
    @Override
    public void onBluetoothEnabledChanged(boolean isEnabled) {

    }

    @Override
    public void onBluetoothTurningOn() {
        startListening();

    }

    @Override
    public void onBluetoothTurningOff() {
        stopListening();
    }

    @Override
    public void onBluetoothIsDiscoverable() {

    }

    @Override
    public void onBluetoothIsConnectable() {

    }

    @Override
    public void onBluetoothIsNotConnectableAndNotDiscoveralbe() {

    }

    @Override
    public void onBluetoothIsNotDiscoveralbe() {

    }
}
