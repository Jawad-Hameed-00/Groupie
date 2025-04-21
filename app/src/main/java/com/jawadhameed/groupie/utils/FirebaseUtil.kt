package com.jawadhameed.groupie.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jawadhameed.groupie.models.UserModel

object FirebaseUtil {

    fun currentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun currentUserDetails(): DocumentReference{
      return FirebaseFirestore.getInstance().collection("Users").document(currentUserID())
    }

    fun allUserCollectionReference(): CollectionReference{
        return FirebaseFirestore.getInstance().collection("Users")
    }

    fun getChatroomReference(chatroomId: String) : DocumentReference{
        return FirebaseFirestore.getInstance().collection("Chatrooms").document(chatroomId)
    }

    fun getChatroomMessageReference(chatroomId: String): CollectionReference{
        return getChatroomReference(chatroomId).collection("Chats")

    }
    fun getChatroomId(userId1: String, userId2: String): String{
        if (userId1.hashCode()< userId2.hashCode()){
            return userId1+ "_" +userId2
        }else{
            return userId2+ "_" +userId1
        }
    }

    fun allChatroomCollectionReference():CollectionReference{
        return FirebaseFirestore.getInstance().collection("Chatrooms")
    }

    fun getOtherUserFromChatroom(userIds: List<String>): DocumentReference{
        if (userIds[0].equals(FirebaseUtil.currentUserID())){
            return allUserCollectionReference().document(userIds[1])
        }else{
            return allUserCollectionReference().document(userIds[0])
        }
    }
}