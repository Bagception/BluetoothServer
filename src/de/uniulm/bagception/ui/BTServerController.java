package de.uniulm.bagception.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.philipphock.android.lib.services.ServiceUtil;
import de.philipphock.android.lib.services.observation.ServiceObservationActor;
import de.philipphock.android.lib.services.observation.ServiceObservationReactor;
import de.uniulm.bagception.bluetoothserver.R;
import de.uniulm.bagception.bluetoothserver.bluetoothstate.BluetoothStateActor;
import de.uniulm.bagception.bluetoothserver.bluetoothstate.BluetoothStateChangeReactor;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerHandler;
import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;

public class BTServerController extends Activity implements ServiceObservationReactor, BluetoothStateChangeReactor{
	private ServiceObservationActor soActor;
	private BluetoothStateActor btStateActor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btserver_controller);
		soActor = new ServiceObservationActor(this, BluetoothServerService.class.getName());
		btStateActor = new BluetoothStateActor(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onServiceStopped(null);
		soActor.register(this);
		btStateActor.register(this);
		//onBluetoothEnabledChanged(BluetoothAdapter.getDefaultAdapter().isEnabled());	
		ServiceUtil.requestStatusForServiceObservable(this, BluetoothServerService.class.getName());
		btStateActor.refireBluetoothCallbacks();
		updateClientConnected();
		registerReceiver(serverStatusListener, new IntentFilter(BluetoothServerHandler.BROADCAST_CLIENTS_CONNECTION_UPDATE));
	}

	
	private void updateClientConnected(){
		TextView v = (TextView) findViewById(R.id.ssClcon);
		v.setText(BluetoothServerHandler.getClientsConnected()+"");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		soActor.unregister(this);
		btStateActor.unregister(this);
		unregisterReceiver(serverStatusListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.btserver_controller, menu);
		return true;
	}
	
	public void onStartStopServerClicket(View v) {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()){
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
			return;
		}
		startStopService();
		
		
		
		

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode==1){
			startStopService();
		}
	}
	
	@Override
	public void onServiceStopped(String serviceName) {

		TextView v = (TextView)findViewById(R.id.serverStatus);
		v.setTextColor(Color.RED);
		v.setText("offline");
		
		Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
		startStopButton.setText("start server");

		
		
	}

	 
	private void startStopService(){
		Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
		
		if(startStopButton.getText().equals("stop server")){
			Intent i = new Intent(this,BluetoothServerService.class);
			stopService(i);	
		}else{
			Intent i = new Intent(this,BluetoothServerService.class);
			startService(i);
		}
		
		
	}
	
	
	public void makeDiscoverable(View v){
		Intent discoverableIntent = new
		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		startActivity(discoverableIntent);
	}

	
	@Override
	public void onBluetoothEnabledChanged(boolean isEnabled) {
		TextView bt = (TextView) findViewById(R.id.btStatus);
		bt.setText(isEnabled ? "enabled":"disabled");
		bt.setTextColor(isEnabled ? Color.GREEN:Color.RED);
		
		if (!isEnabled){
			Button startStopButton = (Button) findViewById(R.id.startStopBTServer);
			startStopButton.setText("enable bluetooth");
		}
	}

	//BT listener
	@Override
	public void onBluetoothTurningOn() {
		
	}

	@Override
	public void onBluetoothTurningOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBluetoothIsDiscoverable() {
		Button makeDiscoverableBtn = (Button) findViewById(R.id.discoverableBtn);
		makeDiscoverableBtn.setEnabled(false);
	}

	@Override
	public void onBluetoothIsConnectable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBluetoothIsNotConnectableAndNotDiscoveralbe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBluetoothIsNotDiscoveralbe() {
		
		Button makeDiscoverableBtn = (Button) findViewById(R.id.discoverableBtn);
		makeDiscoverableBtn.setEnabled(true);
	}
	
	
	
	private final BroadcastReceiver serverStatusListener = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BT","serverStatus recv") ;
			updateClientConnected();
			
		}
	};
	

}
