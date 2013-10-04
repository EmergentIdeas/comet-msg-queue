package com.emergentideas.cometmsgqueue.handles;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.emergentideas.cometmsgqueue.MessageEndpoint;
import com.emergentideas.cometmsgqueue.SwitchingStation;
import com.emergentideas.webhandle.Location;
import com.emergentideas.webhandle.output.ResponsePackage;
import com.emergentideas.webhandle.output.Template;

public class CometHandle {

	@Resource
	protected SwitchingStation switchingStation;
	
	protected int waitTime = 120000;
	
	@GET
	@Path("/comet/messages")
	@Template
	@ResponsePackage("body-only")
	public Object getMessage(HttpServletRequest request, String[] channels, String uid, Location location) {
		
		if(channels == null || channels.length == 0) {
			channels = new String[] { "session", "user" };
		}
		
		Object[] effectiveChannels = new Object[channels.length];
		for(int i = 0; i < channels.length; i++) {
			if("session".equals(channels[i])) {
				effectiveChannels[i] = request.getSession();
			}
			else if("user".equals(channels[i])) {
				if(request.getUserPrincipal() != null) {
					effectiveChannels[i] = request.getUserPrincipal().getName();
				}
				else {
					effectiveChannels[i] = "";
				}
			}
			else {
				effectiveChannels[i] = channels[i];
			}
		}
		
		MessageEndpoint endpoint = new MessageEndpoint(uid, effectiveChannels);
		
		switchingStation.register(endpoint);
		if(endpoint.isMessagePresent() == false) {
			synchronized (endpoint) {
				try {
					endpoint.wait(waitTime);
				}
				catch(Exception e) {}
			}
		}
		
		if(endpoint.isMessagePresent() == false) {
			endpoint.setQueue("noop");
		}
		
		location.add(endpoint);
		
		return endpoint.getTemplateName() != null ? endpoint.getTemplateName() : "comet/message";
	}
}
