package com.emergentideas.cometmsgqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageEndpoint extends Message {

	protected List<Object> channels = Collections.synchronizedList(new ArrayList<Object>());
	
	
	protected boolean messagePresent = false;
	
	protected String uniqueId;
	
	public MessageEndpoint() {}
	
	public MessageEndpoint(String uniqueId, Object ... channels) {
		this.uniqueId = uniqueId;
		for(Object channel : channels) {
			this.channels.add(channel);
		}
	}

	public synchronized List<Object> getChannels() {
		return channels;
	}

	public synchronized void setChannels(List<Object> channels) {
		this.channels = channels;
	}

	public synchronized String getQueue() {
		return queue;
	}

	public synchronized void setQueue(String queue) {
		messagePresent = true;
		this.queue = queue;
	}

	public synchronized Object getData() {
		return data;
	}

	public synchronized void setData(Object data) {
		messagePresent = true;
		this.data = data;
	}

	public synchronized boolean isMessagePresent() {
		return messagePresent;
	}

	public synchronized void setMessagePresent(boolean messagePresent) {
		this.messagePresent = messagePresent;
	}

	public synchronized String getTemplateName() {
		return templateName;
	}

	public synchronized void setTemplateName(String templateName) {
		messagePresent = true;
		this.templateName = templateName;
	}

	public synchronized String getUniqueId() {
		return uniqueId;
	}

	public synchronized void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public synchronized String getContentType() {
		return contentType;
	}

	public synchronized void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	
}
