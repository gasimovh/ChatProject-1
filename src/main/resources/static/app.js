function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
	ws = new WebSocket('ws:'+ window.location.href.slice(5) + '/chat/' + $("#channel").val());
	ws.onmessage = function(data){
		showGreeting(data.data);
	}
	 setConnected(true);
}

function disconnect() {
    /*if (ws != null) {
        ws.close();
    }*/
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    ws.send($("#message").val());
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

