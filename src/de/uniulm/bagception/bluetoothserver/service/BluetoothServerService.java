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
import android.os.IBinder;
import android.util.Log;
import de.philipphock.android.lib.services.observation.ObservableService;
import de.uniulm.bagception.bluetooth.BagceptionBTServiceInterface;
import de.uniulm.bagception.bluetoothserver.service.impl.BluetoothEchoHandler;

public class BluetoothServerService extends ObservableService implements Runnable, BagceptionBTServiceInterface {

	private final int REQUEST_ENABLE_BT = 0;
	private Thread acceptThread;
	private boolean keepAlive = true;
    private BluetoothServerSocket mmServerSocket;
    
    private final int threadpool_thread=5;
    private final int executorCorePoolSize = 1;
    private final int executorMaxPoolSize = 10;
    private final int executorKeepAliveTime = 10;
    
    private final ThreadPoolExecutor executor;
    private BluetoothServerSocket currWaitingSocket;
	
	private ConcurrentHashMap<String, BluetoothServerHandler> handlermap;

    
    public BluetoothServerService() {
    	executor = new ThreadPoolExecutor(executorCorePoolSize, executorMaxPoolSize, executorKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));
	}
    
	/* ============ BluetoothServerServiceControlInterface ============== */
	

	/* ============ Service ============== */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	/* ============ Runnable ============== */

	@Override
	public void run() {
		Log.d("Bluetooth","goto run");
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		while (keepAlive){
			Log.d("Bluetooth","in loop");
			currWaitingSocket = null;
			try {
				currWaitingSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_SERVICE_NAME, UUID.fromString(BT_UUID));
				Log.d("Bluetooth","Waiting for connection");
				BluetoothSocket acceptedSocket = currWaitingSocket.accept();
				Log.d("Bluetooth","connection accepted");
				executor.submit(new BluetoothEchoHandler(this,acceptedSocket));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		for (BluetoothServerHandler handler : handlermap.values()) {
		    handler.close();
		}
		handlermap.clear();
		Log.d("BTS","servier shutdown");
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
		handlermap.remove(btsh);
	}

}
