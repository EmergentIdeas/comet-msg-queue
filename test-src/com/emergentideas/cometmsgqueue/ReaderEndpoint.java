package com.emergentideas.cometmsgqueue;

public class ReaderEndpoint extends Thread {

	protected MessageEndpoint endpoint;
	protected SwitchingStation switchingStation;
	protected String endpointIdentifier;
	
	protected boolean printedMessage = false;
	
	public ReaderEndpoint(SwitchingStation switchingStation, MessageEndpoint endpoint, String endpointIdentifier) {
		super();
		this.endpoint = endpoint;
		this.switchingStation = switchingStation;
		this.endpointIdentifier = endpointIdentifier;
	}

	@Override
	public void run() {
		switchingStation.register(endpoint);
		
		if(endpoint.isMessagePresent() == false) {
			synchronized (endpoint) {
				try {
					endpoint.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		
		if(endpoint.isMessagePresent()) {
			printedMessage = true;
			System.out.println(endpointIdentifier + ": Message is: " + endpoint.getData().toString());
		}
	}

	public boolean isPrintedMessage() {
		return printedMessage;
	}

	public void setPrintedMessage(boolean printedMessage) {
		this.printedMessage = printedMessage;
	}
	
	
}
