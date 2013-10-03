package de.uniulm.bagception.bluetoothserver.service;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothServerService extends Service implements BluetoothServerServiceControlInterface, Runnable, BagceptionBTService {

	private final int REQUEST_ENABLE_BT = 0;
	private Thread acceptThread;
	private boolean keepAlive = true;
    private BluetoothServerSocket mmServerSocket;
    
    private final int threadpool_thread=5;
    private final int executorCorePoolSize = 1;
    private final int executorMaxPoolSize = 10;
    private final int executorKeepAliveTime = 10;
    
    private final ThreadPoolExecutor executor;
    
	
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
		init();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/* ============ Runnable ============== */

	@Override
	public void run() {
		Log.d("Bluetooth","goto run");
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		while (keepAlive){
			Log.d("Bluetooth","in loop");
			BluetoothServerSocket tmp = null;
			try {
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_SERVICE_NAME, UUID.fromString(BT_UUID));
				Log.d("Bluetooth","Waiting for connection");
				BluetoothSocket acceptedSocket = tmp.accept();
				Log.d("Bluetooth","connection accepted");
				executor.submit(new BluetoothServerHandler(acceptedSocket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void init(){
		Log.d("Bluetooth","Server init..");

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
		    // Device does not support Bluetooth
			return;
		}
		
		if (!bluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    //startActivity(enableBtIntent);
		}
		
		
		acceptThread = new Thread(this);
		acceptThread.start();
		Log.d("Bluetooth","Server init done");
	}

}
