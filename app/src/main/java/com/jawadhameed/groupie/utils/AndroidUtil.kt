package com.jawadhameed.groupie.utils

import android.content.Intent
import com.google.firebase.Timestamp
import com.jawadhameed.groupie.models.UserModel

object AndroidUtil {

    fun passUserModelAsIntent(intent: Intent, model: UserModel){
        intent.putExtra("userName", model.userName)
        intent.putExtra("userId", model.userId)
        intent.putExtra("phoneNumber", model.phoneNumber)
        intent.putExtra("profilePhoto", model.profilePhoto)
    }

    fun getUserModelAsIntent(intent: Intent): UserModel {
        val userName = intent.getStringExtra("userName")
        val userId = intent.getStringExtra("userId")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val profilePhoto = intent.getStringExtra("profilePhoto")

        return UserModel(userId!!, phoneNumber!!, userName!!, profilePhoto!!, Timestamp.now())
    }
}