package de.uniulm.bagception.bluetooth.protocol.commandLayer;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONCommandProtocol {

	private final JSONCommandProtocolCallback callback;
	
	public JSONCommandProtocol(final JSONCommandProtocolCallback callback) {
		this.callback=callback;
	}
	
	public void in(String s){
		try {
			JSONObject json = new JSONObject(s);
			String cmd = json.getString("cmd");
			String payload = json.getString("payload");
			callback.onCommandRecv(cmd, payload);
		} catch (JSONException e) {
			//TODO handle
			e.printStackTrace();
		}
	}
	
	public String out(String command,String payload){
		JSONObject json = new JSONObject();
		try {
			json.put("cmd", command);
			json.put("payload", payload);
			return json.toString();
		} catch (JSONException e) {
			try {
				json.put("error", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return json.toString();
	}
}
