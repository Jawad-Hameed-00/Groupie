package com.jawadhameed.groupie.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.jawadhameed.groupie.R
import com.jawadhameed.groupie.models.ChatMessageModel
import com.jawadhameed.groupie.utils.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(
    options: FirestoreRecyclerOptions<ChatMessageModel>,
    private val context: Activity
) : FirestoreRecyclerAdapter<ChatMessageModel, ChatListAdapter.ChatViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessageModel) {
        if (model.senderId == FirebaseUtil.currentUserID()) {
            holder.linearLayoutOther.visibility = View.GONE
            holder.linearLayoutMy.visibility = View.VISIBLE
            holder.myMessage.text = model.message
            holder.myMessageTime.text = firebaseTimestampToTime(model.timestamp)
            setWidth(holder.myMessage)
        } else {
            holder.linearLayoutMy.visibility = View.GONE
            holder.linearLayoutOther.visibility = View.VISIBLE
            holder.otherMessage.text = model.message
            holder.otherUserMessageTime.text = firebaseTimestampToTime(model.timestamp)
            setWidth(holder.otherMessage)
        }
    }

    private fun setWidth(message: TextView) {
        val screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = context.windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

        val maxPercentageWidth = 0.7
        val maxWidth = (screenWidth * maxPercentageWidth).toInt()
        message.maxWidth = maxWidth
    }

    private fun firebaseTimestampToTime(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayoutOther: LinearLayout = itemView.findViewById(R.id.linearLayoutOther)
        val linearLayoutMy: LinearLayout = itemView.findViewById(R.id.linearLayoutMy)
        val myMessage: TextView = itemView.findViewById(R.id.myMessage)
        val otherMessage: TextView = itemView.findViewById(R.id.otherUserMessage)
        val myMessageTime: TextView = itemView.findViewById(R.id.myMessageTime)
        val otherUserMessageTime: TextView = itemView.findViewById(R.id.otherUserMessageTime)
    }
}
