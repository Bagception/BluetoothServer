package de.uniulm.bagception.bluetoothserver.bluetoothstate;

import de.philipphock.android.lib.Reactor;


public interface BluetoothStateChangeReactor extends Reactor{
	public void onBluetoothEnabledChanged(boolean isEnabled);
	
}
