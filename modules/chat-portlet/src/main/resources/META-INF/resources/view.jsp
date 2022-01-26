<%@ include file="/init.jsp" %>

<form>
	<input id="chatMessageText" type="text">
	<input onclick="wsSendMessage();" value="Chat" type="button">
	<input onclick="wsCloseConnection();" value="Disconnect" type="button">
</form>
<br>
<textarea id="echoText" rows="5" cols="30"></textarea>
<script type="text/javascript">
	var webSocket = new WebSocket("ws://localhost:8080/o/websocket/chat/<%= user.getScreenName() %>");
	var echoText = document.getElementById("echoText");

	echoText.value = "";

	var message = document.getElementById("chatMessageText");

	webSocket.onopen = function(message){ wsOpen(message);};
	webSocket.onmessage = function(message){ wsGetMessage(message);};
	webSocket.onclose = function(message){ wsClose(message);};
	webSocket.onerror = function(message){ wsError(message);};

	function wsOpen(message){
		echoText.value += "Connected ... \n";
	}
	function wsSendMessage(){
		var chatMsg = { from: "me", message: message.value };

		webSocket.send(JSON.stringify(chatMsg));
		echoText.value += "Message sent to the server : " + message.value + "\n";
		message.value = "";
	}
	function wsCloseConnection(){
		webSocket.close();
	}
	function wsGetMessage(message){
		echoText.value += "Message received from to the server : " + message.data + "\n";
	}
	function wsClose(message){
		echoText.value += "Disconnect ... \n";
	}

	function wsError(message){
		echoText.value += "Error ... \n";
	}
</script>