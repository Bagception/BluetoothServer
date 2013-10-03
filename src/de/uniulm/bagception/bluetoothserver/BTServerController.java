package de.uniulm.bagception.bluetoothserver;

import de.uniulm.bagception.bluetoothserver.service.BluetoothServerService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class BTServerController extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btserver_controller);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.btserver_controller, menu);
		return true;
	}
	
	public void onStartStopServerClicket(View v) {
		Intent i = new Intent(this,BluetoothServerService.class);
		startService(i);

	}

}
