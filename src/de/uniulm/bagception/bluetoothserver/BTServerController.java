package de.uniulm.bagception.bluetoothserver;

import java.util.concurrent.ConcurrentHashMap;

import de.philipphock.android.lib.services.ServiceUtil;
import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BTServerController extends Activity implements ServiceObservationReactor{
	private ServiceObservationActor soActor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btserver_controller);
		soActor = new ServiceObservationActor(this, BluetoothServerService.class.getName());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onServiceStopped(null);
		soActor.register(this);
		ServiceUtil.requestStatusForServiceObservable(this, BluetoothServerService.class.getName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		soActor.unregister(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.btserver_controller, menu);
		return true;
	}
	
	public void onStartStopServerClicket(View v) {
		
		Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
		
		if(startStopButton.getText().equals("stop server")){
			Intent i = new Intent(this,BluetoothServerService.class);
			stopService(i);	
		}else{
			Intent i = new Intent(this,BluetoothServerService.class);
			startService(i);
		}
		
		
		

	}

	@Override
	public void onServiceStarted(String serviceName) {
		TextView v = (TextView)findViewById(R.id.serverStatus);
		v.setTextColor(Color.GREEN);
		v.setText("online");
		
		Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
		startStopButton.setText("stop server");
	}

	@Override
	public void onServiceStopped(String serviceName) {
		TextView v = (TextView)findViewById(R.id.serverStatus);
		v.setTextColor(Color.RED);
		v.setText("offline");
		
		Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
		startStopButton.setText("start server");
	}


}
