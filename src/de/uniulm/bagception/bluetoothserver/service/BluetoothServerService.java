package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import de.philipphock.android.lib.services.observation.ObservableService;
import de.uniulm.bagception.bluetooth.BagceptionBTServiceInterface;
import de.uniulm.bagception.bluetoothserver.service.impl.BluetoothEchoHandler;
import de.uniulm.bagception.bluetoothserver.service.impl.JSONCommandProtocolHandler;
import de.uniulm.bagception.bluetoothserver.service.impl.PayloadContentLengthProtocolHandler;

public class BluetoothServerService extends ObservableService implements Runnable, BagceptionBTServiceInterface {

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
    
	//IPC
	private final Messenger incomingMessenger;
    private final Handler incomingHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
				case MESSAGE_TYPE_SENDMESSAGE:
					sendToAll((String)msg.obj);
					break;
			}
			return true;
		}
	});
    
    public BluetoothServerService() {
    	incomingMessenger = new Messenger(incomingHandler);
    	this.handlerFactory = new HandlerFactory() {
			
			@Override
			public BluetoothServerHandler createHandler(BluetoothServerService service,
					BluetoothSocket socket) {
				return new PayloadContentLengthProtocolHandler(service,socket);
				//return new BluetoothEchoHandler(service, socket);
			}
		};
    	executor = new ThreadPoolExecutor(executorCorePoolSize, executorMaxPoolSize, executorKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));
	}
    
	/* ============ BluetoothServerServiceControlInterface ============== */
	

	/* ============ Service ============== */
	@Override
	public IBinder onBind(Intent arg0) {
		return incomingMessenger.getBinder();
	}

	
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
		
		Log.d("Bluetooth","Server init..");

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
		Log.d("Bluetooth","Server init done");
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
		Log.d("bt","remove handler from map");
		Log.d("bt"," "+handlermap.size()+" ");
		handlermap.remove(btsh.toString());
		Log.d("bt"," "+handlermap.size()+" "); 
		
	}

	
	private void sendToAll(String s){
		for (BluetoothServerHandler handler : handlermap.values()) {
		    ((PayloadContentLengthProtocolHandler)handler).send(s);
		}
	}
}
