var stompClient = null;
var current_chat_id = "";
var current_user_id = "";
var receiver_id = 0;
var first_load = true;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#send_form").show();
    }
    else {
        $("#send_form").hide();
    }
}

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/users/online', function (data) {
            getCurrentChatId(JSON.parse(data.body));
            getChats();
        });
        getChats();
        getNewMessages();
    });
}

function getCurrentChatId(data) {
    new_chat_id = data.new_chat_id;
    if(current_chat_id == 0 & new_chat_id != 0) {
        current_chat_id = new_chat_id;
    }
}

$(function() {
    getCurrentUserId();
    connect();
});

function getCurrentUserId(){
    $.ajax({
        url: '/current/user',
        method: 'get',
        dataType: 'json',
        success: function(id){
            current_user_id = id;
        }
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    var end_point = "/app/message/" + current_chat_id + "/" + receiver_id;
    var text = $("#text").val();
    stompClient.send(end_point, {}, JSON.stringify({'text': text}));
    appendMessage(text);
}

function appendMessage(text) {
    var time = new Date().toTimeString().substring(0, 5);
    $("#messages").append(
            '<div class="card w-75 m-2 bg-light" align="right">' +
                '<div id="" class="card-body p-2">' +
                    '<p class="card-text">' + text + '<i>&nbsp;&nbsp;' + time + '</i></p>' +
                '</div>' +
            '</div>');
}

function setUserOffLine() {
     $.ajax({
     	url: '/off/line',
     	method: 'get',
     	dataType: 'json'
     });
}

function getChats() {
    $.ajax({
    	url: '/chats',
    	method: 'get',
    	dataType: 'json',
    	success: function(data){
    		updateChats(data);
    	}
    });
}

function updateChats(data) {
    $("#chats").empty();
    $.each(data, function(key, value) {
        if(current_chat_id == "") {
            current_chat_id = value.id;
        }
        var next_li = '<li id="' + value.user_id + '" chat_id="' + value.id + '"  class="list-group-item">' +
                      '<img src="/avatars/' + value.user_id + '.png" width=40 class="align-middle me-2" title="avatar">' +
                      '<span>' + value.name + '</span>';
        if(value.online == true) {
            next_li += '<img src="icons/online.png" class="align-middle mx-1 mb-1" width="15" title="online">';
        }
        next_li += '<span id="new_messages"></span></li>';
        $("#chats").append(next_li);
        if(value.id != 0) {
            subscribeToChat(value.id);
        }
    });
    if(first_load == true){
        getMessages(current_chat_id);
        first_load = false;
    }
}

// TODO onGetNewMessages setMessagesRead

function getNewMessages() {
    $.ajax({
        url: '/messages/new',
        method: 'get',
        dataType: 'json',
        success: function(data){
        	$(document).ready(showNewMessages(data));
        }
    });
}

function showNewMessages(data) {
    Object.entries(data).forEach(([key, value]) => addNewMessagesToChat(key, value));
}

function addNewMessagesToChat(chat_id, value) {
    console.log(chat_id);
    console.log(value);
    console.log($("#chats").find(`[chat_id='${chat_id}']`));
    var span = $("#chats").find(`[chat_id='${chat_id}']`).find('#new_messages');
    console.log(span)
    span.text(value);

}

function showNotification(notification) {
    console.log(notification);
    chat_id = notification.chat;
    if(chat_id == current_chat_id) {
        getMessages(current_chat_id);
    } else {
        var span = $("#chats").find(`[chat_id='${chat_id}']`).find('#new_messages');
        var message_number = span.text();
        span.text(++message_number);
    }
}

function subscribeToChat(chat_id) {
    stompClient.subscribe('/queue/notification/' + chat_id, function (notification) {

        console.log(notification); // TODO is .body necessary

        showNotification(JSON.parse(notification.body));
    });
}

// TODO on start getNotificationForNewMessages()

function showChatMessages(element) {
    receiver_id = element.target.id != "" ? element.target.id : element.target.parentElement.id;
    current_chat_id = $('#chats').find('[id="' + receiver_id + '"]').attr('chat_id');
    $("#chats").find(`[chat_id='${current_chat_id}']`).find('#new_messages').text("");
    getMessages(current_chat_id);
}

function getMessages(chat_id) {

    if(chat_id == 0) {
        $("#messages").empty();
        return;
    }

    // TODO complete pagination to /message/list
    page = 0;
    size = 10;

    $.ajax({
    	url: '/message/list/' + chat_id + '?page=' + page + '&size=' + size,
    	method: 'get',
    	dataType: 'json',
    	success: function(data){
    		updateMessages(data);
    	}
    });
}

function updateMessages(data) {
    $("#messages").empty();
    $.each(data, function(key, value) {
        var next_message =
        '<div class="card w-75 m-2 ' + (value.user_id == current_user_id ? 'bg-light" align="right"' : '"') + '>' +
            '<div id="' + value.id + '" class="card-body p-2">' +
                '<p class="card-text">' + value.text + '<i>&nbsp;&nbsp;' + value.time.substring(0, 5) + '</i></p>' +
            '</div>' +
        '</div>';
        $("#messages").append(next_message);
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function() { connect(); });
    $("#disconnect").click(function() { disconnect(); });
    $("#send").click(function() {sendMessage(); });
    $("#chats").click(function(element) { showChatMessages(element);});
});

$(window).on('beforeunload', function() {
    setUserOffLine();
    disconnect();
});