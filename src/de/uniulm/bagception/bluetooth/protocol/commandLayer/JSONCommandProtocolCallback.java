package de.uniulm.bagception.bluetooth.protocol.commandLayer;

public interface JSONCommandProtocolCallback {
	public void onCommandRecv(String cmd, String payload);
}
