package com.jawadhameed.groupie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.jawadhameed.groupie.ChatActivity
import com.jawadhameed.groupie.R
import com.jawadhameed.groupie.models.ChatroomModel
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.AndroidUtil
import com.jawadhameed.groupie.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale

class RecentChatAdapter(var chatroomList: List<ChatroomModel>, val context: Context) : RecyclerView.Adapter<RecentChatAdapter.RecentChatViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.recent_chat_list_item, parent, false)
        return RecentChatViewHolder(view)
    }

    override fun getItemCount(): Int {
       return chatroomList.filter { it.userIDs.contains(FirebaseUtil.currentUserID()) }.size
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        val model = chatroomList.filter { it.userIDs.contains(FirebaseUtil.currentUserID()) }[position]

        FirebaseUtil.getOtherUserFromChatroom(model.userIDs).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastMessageSendByMe = model.lastMessageSenderId == FirebaseUtil.currentUserID()
                    val userModel = task.result.toObject(UserModel::class.java)
                    holder.userName.text = userModel?.userName
                    if (lastMessageSendByMe) {
                        holder.lastMessage.text = "You: ${model.lastMessage}"
                    } else {
                        holder.lastMessage.text = model.lastMessage
                    }
                    holder.lastMessageTime.text = firebaseTimestampToTime(model.timestamp)
                    Glide.with(context).load(userModel?.profilePhoto).placeholder(R.drawable.person).into(holder.profilePhoto)

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, ChatActivity::class.java)
                        userModel?.let { user ->
                            AndroidUtil.passUserModelAsIntent(intent, user)
                        }
                        context.startActivity(intent)
                    }
                }
            }

    }

    fun updateData(chatrooms: List<ChatroomModel>) {
        chatroomList = chatrooms.sortedByDescending { it.timestamp.toDate() }
        notifyDataSetChanged()
    }

    private fun firebaseTimestampToTime(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = timestamp.toDate()
        return sdf.format(date)
    }

    inner class RecentChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var userName: TextView
        var lastMessage: TextView
        var lastMessageTime: TextView
        var profilePhoto: CircleImageView
        init {
            userName = itemView.findViewById(R.id.userNameText)
            lastMessage = itemView.findViewById(R.id.lastMessageText)
            lastMessageTime = itemView.findViewById(R.id.lastMessageTime)
            profilePhoto = itemView.findViewById(R.id.profilePhotoImage)
        }
    }
}