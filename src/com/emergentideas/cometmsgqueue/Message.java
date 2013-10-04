package com.emergentideas.cometmsgqueue;

public class Message {
	protected String queue;
	protected Object data;
	protected String templateName;
	protected String contentType = "text/html";
	
	public Message() {}
	
	public Message(String queue, String templateName, String contentType, Object data) {
		super();
		this.queue = queue;
		this.data = data;
		this.templateName = templateName;
		this.contentType = contentType;
	}

	public String getQueue() {
		return queue;
	}
	public void setQueue(String queue) {
		this.queue = queue;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	

}
