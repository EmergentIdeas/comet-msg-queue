package com.emergentideas.cometmsgqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageWaitingQueue {

	protected List<Object> channels = Collections.synchronizedList(new ArrayList<Object>());
	protected String uniqueId;
	protected long createdDate;
	protected List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());
	
	public MessageWaitingQueue() {
		createdDate = System.currentTimeMillis();
	}
	
	public MessageWaitingQueue(MessageEndpoint endpoint) {
		this();
		this.channels.addAll(endpoint.getChannels());
		this.uniqueId = endpoint.getUniqueId();
	}

	public List<Object> getChannels() {
		return channels;
	}

	public void setChannels(List<Object> channels) {
		this.channels = channels;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	
}
