(function() {
	var genId = function(idLength) {
		var uid = 'id';
		while(idLength > 0) {
			var digit = Math.floor((Math.random()*10));
			uid += digit;
			idLength--;
		}
		return uid;
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

