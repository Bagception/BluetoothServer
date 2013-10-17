package de.uniulm.bagception.bluetooth.protocol;

import android.util.Log;

public class PayloadContentLengthProtocol {

	private final StringBuilder head;
	private final StringBuilder message;
	private int messageLength=-1;
	private boolean headerRecv = false;
	private final PayloadContentLengthProtocolCallback callback;
	public PayloadContentLengthProtocol(PayloadContentLengthProtocolCallback callback) {
		head = new StringBuilder();
		message = new StringBuilder();
		this.callback=callback;
	}
	
	public void in(String c){
		if (c.length()==0){
			Log.d("RECV","len = 0");
			return;
		}
		if (headerRecv){
			//header complete recv
			int messageLength = message.length()+c.length(); //the length of the current  message + all currently recv bytes
			if (messageLength<this.messageLength){
				//all currently recv bytes are part of the message
				message.append(c);
			}else{
				//the recv bytes contain a new message or is empty
				int nextMessage = (messageLength - this.messageLength); //offset of the next message
				int partMessageOnly = c.length()-nextMessage; //offset of the end of this message
				
				String partMessageOnlyString = c.substring(0,partMessageOnly); //the string that contains the rest of this message
				message.append(partMessageOnlyString);
				callback.onMessageRecv(message.toString());
				message.setLength(0);
				in(c.substring(partMessageOnly));
			}
		}else{
			int headerSeparator = c.indexOf(':');
			if (headerSeparator == -1){
				head.append(c);
			}else{
				headerRecv=true;
				String head = c.substring(0, headerSeparator);
				String msgContinue = c.substring(headerSeparator+1);
				this.head.append(head);
				this.messageLength=Integer.parseInt(head.toString());
				Log.d("RECV","messageLength "+messageLength);
				in(msgContinue);
			}
		}
	}
}
