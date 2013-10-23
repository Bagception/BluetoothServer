package de.uniulm.bagception.bluetooth.protocol.messageLayer;




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
	
	public synchronized void in(String c){
		if (c.length()==0){
			return;
		}
		
		if (headerRecv){
			inMessage(c);
		}else{
			inHeader(c);
		}
	}
	
	private void inHeader(String h){

		if (h.length()==0) return;
		
		//probing header 
		if (head.length()==0){
			try{
				
			}catch(NumberFormatException e){
				reset();
				return;
			}
		}
		
		int headerSeparator = h.indexOf(':');
		
		if (headerSeparator == -1){
			head.append(h);
		}else{
			headerRecv=true;
			String head = h.substring(0, headerSeparator);
			String msgContinue = h.substring(headerSeparator+1);
			this.head.append(head);
			
			try{
				this.messageLength=Integer.parseInt(this.head.toString());	
			}catch(NumberFormatException e){
				reset();
			}
			inMessage(msgContinue);
		}
	}
	
	private void inMessage(String m){
		if (m.length()==0)return;
		headerRecv=false;
		//header complete recv
		int messageLength = message.length()+m.length(); //the length of the current  message + all currently recv bytes
		if (messageLength<this.messageLength){
			//all currently recv bytes are part of the message
			headerRecv=true;
			message.append(m);
		}else{
			//the recv bytes contain a new message or is empty
			int partMessageOnly =  this.messageLength-message.length(); //offset of the next message
			
			String tail = m.substring(partMessageOnly);
			String partMessageOnlyString = m.substring(0,partMessageOnly); //the string that contains the rest of this message
			message.append(partMessageOnlyString);
			callback.onMessageRecv(message.toString());
			head.setLength(0);
			message.setLength(0);
			
			inHeader(tail);

		}
	}
	
	public String out(String msg){
		int l = msg.length();
		StringBuilder sb = new StringBuilder(msg.length()+4);
		sb.append(l+":"+msg);
		return sb.toString();
	}
	
	

	private void reset(){
		head.setLength(0);
		message.setLength(0);
		headerRecv=false;
	}
}
