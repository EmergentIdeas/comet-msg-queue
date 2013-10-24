(function() {
	var getCookie = function(c_name) {
		var c_value = document.cookie;
		var c_start = c_value.indexOf(" " + c_name + "=");
		if (c_start == -1) {
			c_start = c_value.indexOf(c_name + "=");
		}
		if (c_start == -1) {
			c_value = null;
		} else {
			c_start = c_value.indexOf("=", c_start) + 1;
			var c_end = c_value.indexOf(";", c_start);
			if (c_end == -1) {
				c_end = c_value.length;
			}
			c_value = unescape(c_value.substring(c_start, c_end));
		}
		return c_value;
	}
	var genId = function(idLength) {
		var uid = 'id';
		while(idLength > 0) {
			var digit = Math.floor((Math.random()*10));
			uid += digit;
			idLength--;
		}
		return getCookie('JSESSIONID') + uid;
	}; 
CometMsgQueue = {
		delay: 10,
		errorDelay: 1000,
		url: '/comet/messages',
		parameters: {
			uid: genId(15),
			channels: []
		},
		messageHandle: function(/* String */ queue, /* Object */ data, /* jQuery request object */ request) {
			
		},
		queryHandle: function(data, textStatus, request) {
			var queue = request.getResponseHeader('Message-Queue');
			CometMsgQueue.messageHandle(queue, data, request);
		},
		start: function() {
			setTimeout('CometMsgQueue.longPoll()', 10);
		},
		longPoll: function() {
			try {
				$.get(CometMsgQueue.getURLWithParms(), CometMsgQueue.queryHandle)
				.done(
						function() {
							setTimeout('CometMsgQueue.longPoll()', CometMsgQueue.delay);
						}
					)
				.fail(
						function() {
							setTimeout('CometMsgQueue.longPoll()', CometMsgQueue.errorDelay);
						}
					)
				;
			}
			catch(e) {
			}
		},
		getURLWithParms: function() {
			return CometMsgQueue.url + '?' + $.param(CometMsgQueue.parameters);
		}
		
};
})();

