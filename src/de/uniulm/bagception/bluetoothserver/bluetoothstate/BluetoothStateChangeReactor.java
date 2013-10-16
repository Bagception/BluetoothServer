package de.uniulm.bagception.bluetoothserver.bluetoothstate;

import de.philipphock.android.lib.Reactor;


public interface BluetoothStateChangeReactor extends Reactor{
	public void onBluetoothEnabledChanged(boolean isEnabled);
	public void onBluetoothTurningOn();
	public void onBluetoothTurningOff();
	
	public void onBluetoothIsDiscoverable();
	public void onBluetoothIsConnectable();
	public void onBluetoothIsNotConnectableAndNotDiscoveralbe();
	public void onBluetoothIsNotDiscoveralbe();
	
}
