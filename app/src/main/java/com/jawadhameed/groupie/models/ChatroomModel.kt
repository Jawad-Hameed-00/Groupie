package com.jawadhameed.groupie.models

import com.google.firebase.Timestamp

data class ChatroomModel(
    val chatroomId: String,
    val userIDs: List<String>,
    var timestamp: Timestamp,
    var lastMessageSenderId: String,
    var lastMessage: String
){
    constructor(): this("", emptyList(), Timestamp.now(), "","" )
}
