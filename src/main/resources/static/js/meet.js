// Declare variables: record your login name and the other party's login name
var name, connectedUser;
var myName;

// Establish WebSocket connection signaling server
var connection = new WebSocket("ws://127.0.0.1:8080/websocketRTC");
// var connection = new WebSocket("wss://localhost:9443/websocketRTC");

// own RTCPeerConnection - The most important object of RTC
var yourConnection;

// Open connection event response
connection.onopen = function() {
	console.log("Connected");
};

// Handle all messages through this callback
connection.onmessage = function(message) {
	console.log("Got message", message.data);

	var data = JSON.parse(message.data);

	switch (data.type) {
		case "login":
			onLogin(data.success);
			break;
		case "offer":
			onOffer(data.offer, data.name);
			break;
		case "answer":
			onAnswer(data.answer);
			break;
		case "candidate":
			onCandidate(data.candidate);
			break;
		case "leave":
			onLeave();
			break;
		default:
			console.log("default message");
			console.log(data);
			break;
	}
};

connection.onerror = function(err) {
	console.log("Got error", err);
};

// Method of sending message to signaling server
function send(message) {
	if (connectedUser) {
		message.name = connectedUser;
		message.myName = name;
	}
	connection.send(JSON.stringify(message));
};

// Bind some tags on HTML
var loginPage = document.querySelector('#login-page'),
	usernameInput = document.querySelector('#username'),
	loginButton = document.querySelector('#login'),
	callPage = document.querySelector('#call-page'),
	chatPage = document.querySelector('#chat-page'),
	theirUsernameInput = document.querySelector('#their-username'),
	callButton = document.querySelector('#call'),
	hangUpButton = document.querySelector('#hang-up'),
	messageInput = document.querySelector('#message'),
	sendButton = document.querySelector('#send'),
	received = document.querySelector('#received');

callPage.style.display = "none";
chatPage.style.display = "none";

// Login button click event response - Login when the user clicks the button - Record the login name and send the login information to the signaling server
loginButton.addEventListener("click", function(event) {
	name = usernameInput.value;
	myName = usernameInput.value;

	if (name.length > 0) {
		send({
			type: "login",
			name: name
		});
	}
});

// Respond to the login information returned by the signaling server
function onLogin(success) {
	if (success === false) {
		alert("Login unsuccessful, please try a different name.");
	} else {
		loginPage.style.display = "none";
		callPage.style.display = "block";
		chatPage.style.display = "block";
		// Get the plumbing ready for a call
		// ready to start a connection
		initialize();
	}
};

// yourConnection, connectedUser, stream, dataChannel;
var yourVideo = document.querySelector('#yours'),
	theirVideo = document.querySelector('#theirs'),
	connectedUser, stream, dataChannel;

function hasRTCPeerConnection() {
	window.RTCPeerConnection = window.RTCPeerConnection || window.webkitRTCPeerConnection || window.mozRTCPeerConnection;
	window.RTCSessionDescription = window.RTCSessionDescription || window.webkitRTCSessionDescription || window.mozRTCSessionDescription;
	window.RTCIceCandidate = window.RTCIceCandidate || window.webkitRTCIceCandidate || window.mozRTCIceCandidate;
	return !!window.RTCPeerConnection;
}

// Turn on your camera - ready to start a peer to peer connection
function initialize() {
	var constraints = { audio: true, video: { width: 320, height: 480 } };
	
	navigator.mediaDevices.getUserMedia(constraints)
		.then(function(mediaStream) {
			yourVideo.srcObject = mediaStream;

			if (hasRTCPeerConnection()) {
				console.log("setupPeerConnection .. ")
				setupPeerConnection(mediaStream);
			} else {
				alert("Sorry, your browser does not support WebRTC.");
			}

			yourVideo.onloadedmetadata = function(e) {
				yourVideo.play();
			};
		})
		.catch(function(err) {
			console.log(err.name + " -- : " + err.message);
		});

}
var handleDataChannelOpen = function (event) {
    console.log("dataChannel.OnOpen", event);
    dataChannel.send(name + " has connected.");
};
var handleDataChannelMessageReceived = function (event) {
    console.log("dataChannel.OnMessage:", event);
    received.innerHTML += "Them: "+event.data + "<br />";
	received.scrollTop = received.scrollHeight;
};
var handleDataChannelError = function (error) {
    console.log("dataChannel.OnError:", error);
};
var handleDataChannelClose = function (event) {
    dataChannel.send(name + " has disconnected.");
    console.log("dataChannel.OnClose", event);
};
var handleChannelCallback = function (event) {
     dataChannel = event.channel;
     dataChannel.onopen = handleDataChannelOpen;
     dataChannel.onmessage = handleDataChannelMessageReceived;
     dataChannel.onerror = handleDataChannelError;
     dataChannel.onclose = handleDataChannelClose;
  };
  
// Create RTCPeerConnection object, bind ICE server, bind multimedia data stream
function setupPeerConnection(stream) {
	if (yourConnection == null) {
		var configuration = {
			"iceServers": [{
				"url": "stun:stun2.1.google.com:19302"
			}]
		};
		yourConnection = new RTCPeerConnection(configuration);
	}


	if (yourConnection == null) {
		console.log("yourConnection is null");
	} else {
		console.log("yourConnection is a object")
	}

	console.log("========================= setupPeerConnection stream ====================================")

	yourConnection.addStream(stream);
	yourConnection.onaddstream = function(e) {
		console.log(e);
		theirVideo.srcObject = e.stream;
		theirVideo.play();
	};

	// Setup ice handling
	yourConnection.onicecandidate = function(event) {
		if (event.candidate) {
			send({
				type: "candidate",
				candidate: event.candidate
			});
		}
	};
	
	yourConnection.ondatachannel = handleChannelCallback;
	// Open the data channel (this is for text communication)
	openDataChannel();
}

function openDataChannel() {
	dataChannel = yourConnection.createDataChannel("dataChannel", {
        reliable : true
    })
  dataChannel.onopen = handleDataChannelOpen;
  dataChannel.onmessage = handleDataChannelMessageReceived;
  dataChannel.onerror = handleDataChannelError;
  dataChannel.onclose = handleDataChannelClose;
}

// Bind our text input and received area
sendButton.addEventListener("click", function(event) {
	var val = messageInput.value;
	received.innerHTML += "You: "+val + "<br />";
	received.scrollTop = received.scrollHeight;
	dataChannel.send(val);
});

callButton.addEventListener("click", function() {
	var theirUsername = theirUsernameInput.value;
	console.log("call " + theirUsername)
	if (theirUsername.length > 0) {
		startPeerConnection(theirUsername);
	}
});

// start peer to peer connection
function startPeerConnection(user) {
	connectedUser = user;

	// yourConnection
	// Begin the offer

	// send call request 1
	yourConnection.createOffer(function(offer) {
		console.log("yourConnection.createOffer");
		send({
			type: "offer",
			offer: offer
		});

		console.log("yourConnection.setLocalDescription(offer);");
		yourConnection.setLocalDescription(offer);
	}, function(error) {
		alert("An error has occurred.");
	});
};

// Accept the caller's response to call request 2
function onOffer(offer, name) {
	connectedUser = name;

	//console.log("============================================================");
	console.log("===============    onOffer       (===================");
	console.log("connector user name is " + connectedUser);
	//console.log("============================================================");
	
	var offerJson = JSON.parse(offer);
	var sdp = offerJson.sdp;
	// Set the conversation description of the other party
	try {
		console.log("yourConnection.setRemoteDescription");
		yourConnection.setRemoteDescription(new window.RTCSessionDescription(offerJson), function() {
			console.log("success");
		}
			,
			function() {
				console.log("fail")
			});

	} 
	catch (e) {
		alert(e)
	}

	// Send a reply message to the call requester 3
	yourConnection.createAnswer(function(answer) {
		yourConnection.setLocalDescription(answer);
		console.log("yourConnection.createAnswer");
		send({
			type: "answer",
			answer: answer
		});
	}, function(error) {
		alert("An error has occurred");
	});

	console.log("onOffer is success");

};

// call requester processing reply 4
function onAnswer(answer) {
	if (yourConnection == null) {
		alert("yourconnection is null in onAnswer");
	}

	//console.log("============================================================");
	console.log("================ OnAnswer ============================");
	//console.log("============================================================");
	console.log(answer);
	if (answer != null) {
		console.log(typeof answer);
	}

	var answerJson = JSON.parse(answer);
	console.log(answerJson);

	try {

		// Set the description of this session
		yourConnection.setRemoteDescription(new RTCSessionDescription(answerJson));
	} 
	catch (e) {
		alert(e);
	}

	console.log("onAnswer is success");

};

// Respond to the ICE candidate connection
function onCandidate(candidate) {
	//console.log("============================================================");
	console.log("================ OnCandidate ============================");
	//console.log("============================================================");
	console.log(candidate);
	if (candidate != null) {
		console.log(typeof candidate);
	}

	var iceCandidate;

	var candidateJson = JSON.parse(candidate);
	console.log(candidateJson);

	iceCandidate = new RTCIceCandidate(candidateJson);

	if (yourConnection == null) {
		alert("yourconnection is null in onCandidate");
	}
	// yourConnection.addIceCandidate(new RTCIceCandidate(candidate));
	yourConnection.addIceCandidate(iceCandidate);
};

hangUpButton.addEventListener("click", function() {
	send({
		type: "leave"
	});

	onLeave();
});

function onLeave() {
	//received.innerHTML += connectedUser + "has disconnected! <br />";
	//received.scrollTop = received.scrollHeight;
	connectedUser = null;
	theirVideo.src = null;
	yourConnection.close();
	yourConnection.onicecandidate = null;
	yourConnection.onaddstream = null;
	setupPeerConnection(stream);
};

