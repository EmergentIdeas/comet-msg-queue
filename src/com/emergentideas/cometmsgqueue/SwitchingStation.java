package com.emergentideas.cometmsgqueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Resource;

/**
 * A class that allows thread to register message endpoints which they can wait on until
 * the switching station notifies the message endpoint that a message is ready.
 * @author kolz
 *
 */
@Resource
public class SwitchingStation {
	
	protected Map<Thread, MessageEndpoint> endpoints = new WeakHashMap<Thread, MessageEndpoint>();
	protected Map<String, MessageWaitingQueue> queues = Collections.synchronizedMap(new HashMap<String, MessageWaitingQueue>());
	
	// The maximum time we'll keep messages around for comet client before we discard them.
	protected long maxWaitingTime = 5000;
	
	/**
	 * Registers an endpoint for the current thread. A weak hash map will be used so that if the 
	 * registering thread goes away the endpoint will also be collected.
	 * @param endpoint
	 */
	public void register(MessageEndpoint endpoint) {
		
		// check to see if there are messages for this id
		// don't check the channels because the previous messages are queued under the
		// assumption of having requested the previous channels
		// However, do update the channels
		// Don't remove the queue but do update the timestamp
		synchronized (queues) {
			if(queues.containsKey(endpoint.getUniqueId())) {
				MessageWaitingQueue q = queues.get(endpoint.getUniqueId());
				if(q != null) {
					if(q.getMessages().size() > 0) {
						q.setChannels(endpoint.getChannels());
						q.setCreatedDate(System.currentTimeMillis());
						Message message = q.getMessages().remove(0);
						copyMessageToEndpoint(endpoint, message);
						endpoint.setMessagePresent(true);
						return;
					}
				}
				// this is where there are no messages or
				// some weird case where the key is present but the object is not
				queues.remove(endpoint.getUniqueId());
			}
		}
		
		// Add this endpoint to those waiting
		synchronized (endpoints) {
			endpoints.put(Thread.currentThread(), endpoint);
		}
	}
	
	protected void copyMessageToEndpoint(MessageEndpoint endpoint, Message message) {
		endpoint.setQueue(message.getQueue());
		String contentType = message.getContentType();
		if(contentType != null) {
			endpoint.setContentType(contentType);
		}
		String templateName = message.getTemplateName();
		if(templateName != null) {
			endpoint.setTemplateName(templateName);
		}
		endpoint.setData(message.getData());
	}
	
	public void sendMessage(Object channel, String queue, Object data) {
		sendMessage(channel, queue, null, null, data);
	}
	/**
	 * Sends a message on a given channel to anybody is listening.
	 * @param channel The channel which an endpoint must be listing to to be notified
	 * @param queue The queue name on which the message should be distributed
	 * @param data The data to send to the listeners
	 */
	public void sendMessage(Object channel, String queue, String templateName, String contentType, Object data) {
		List<MessageEndpoint> endpointsToNofify = new ArrayList<MessageEndpoint>();
		List<MessageWaitingQueue> newWaiting = new ArrayList<MessageWaitingQueue>();
		synchronized (endpoints) {
			Iterator<MessageEndpoint> it = endpoints.values().iterator();
			while(it.hasNext()) {
				MessageEndpoint endpoint = it.next();
				for(Object endpointChannel : endpoint.getChannels()) {
					if(endpointChannel.equals(channel)) {
						endpointsToNofify.add(endpoint);
						it.remove();
						MessageWaitingQueue waiting = new MessageWaitingQueue(endpoint);
						newWaiting.add(waiting);
						break;
					}
				}
			}
		}
		
		synchronized (queues) {
			long currentTime = System.currentTimeMillis();
			Iterator<MessageWaitingQueue> it = queues.values().iterator();
			while(it.hasNext()) {
				MessageWaitingQueue waiting = it.next();
				if((currentTime - waiting.getCreatedDate()) > maxWaitingTime) {
					it.remove();
				}
				else {
					for(Object endpointChannel : waiting.getChannels()) {
						if(endpointChannel.equals(channel)) {
							Message message = new Message(queue, templateName, contentType, data);
							waiting.getMessages().add(message);
							break;
						}
					}
				}
			}
			
			for(MessageWaitingQueue q : newWaiting) {
				queues.put(q.getUniqueId(), q);
			}
		}

		
		
		for(MessageEndpoint endpoint : endpointsToNofify) {
			synchronized (endpoint) {
				endpoint.setQueue(queue);
				if(templateName != null) {
					endpoint.setTemplateName(templateName);
				}
				endpoint.setData(data);
				if(contentType != null) {
					endpoint.setContentType(contentType);
				}
				endpoint.notifyAll();
			}
		}
	}
	
	/**
	 * Removes the endpoint for the current thread.
	 */
	public void removeEndpoint() {
		synchronized (endpoints) {
			endpoints.remove(Thread.currentThread());
		}
	}

	public long getMaxWaitingTime() {
		return maxWaitingTime;
	}

	public void setMaxWaitingTime(long maxWaitingTime) {
		this.maxWaitingTime = maxWaitingTime;
	}

	
}
