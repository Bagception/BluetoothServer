package de.uniulm.bagception.bluetooth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

public class BluetoothDiscovery implements BagceptionBTServiceInterface{
	private BluetoothAdapter bluetoothDevice = null;
	private List<Void> devicesRunningBagceptionService = Collections.synchronizedList(new ArrayList<Void>());

	public BluetoothDiscovery() {
		reInit();
	}
	
	public void reInit(){
		bluetoothDevice = BluetoothAdapter.getDefaultAdapter();
		devicesRunningBagceptionService.clear();
	}
	
	private BluetoothAdapter getAdapter(){
		if (bluetoothDevice == null){
			reInit();
		}
		return bluetoothDevice;
	}
	
	public void scanForNewDevices(){
		getAdapter().startDiscovery();
	}
	
	public List<BluetoothDevice> getPairedDevicesWithBagceptionService(){
		List<BluetoothDevice> ret = new ArrayList<BluetoothDevice>();
		
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    	if (pairedDevices.size() > 0) {
    	    for (BluetoothDevice device : pairedDevices) {
    	    	for (ParcelUuid uuid:device.getUuids()){
    	    		if (uuid.toString().equals(BT_UUID)){
    	    			ret.add(device);	    	    			
    	    		}
    	    	}
    	    	
    	    }
    	}
    	return ret;
	}
	
}
