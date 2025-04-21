package com.jawadhameed.groupie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.jawadhameed.groupie.adapter.ChatListAdapter
import com.jawadhameed.groupie.databinding.ActivityChatBinding
import com.jawadhameed.groupie.models.ChatMessageModel
import com.jawadhameed.groupie.models.ChatroomModel
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.AndroidUtil
import com.jawadhameed.groupie.utils.FirebaseUtil


class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var model: UserModel
    lateinit var chatroomId: String
    private var chatroomModel: ChatroomModel? = null
    private var adapter: ChatListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = AndroidUtil.getUserModelAsIntent(intent)
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserID(), model.userId)


        binding.userNameText.setText(model.userName)
        binding.backButton.setOnClickListener {
            finish()
        }

        Glide.with(this).load(model.profilePhoto).placeholder(R.drawable.person)
            .into(binding.profilePhoto)


        binding.sendMessageButton.setOnClickListener {
            val message = binding.messageEdittext.text.toString().trim()
            if (message.isEmpty()) {
                return@setOnClickListener
            }

            sendMessageToUser(message)
        }

        getOrCreateChatRoomModel()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val query = FirebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        adapter = ChatListAdapter(options, this@ChatActivity)
        val manager = LinearLayoutManager(this@ChatActivity)
        manager.reverseLayout = true
        binding.chatRecyclerView.layoutManager = manager
        binding.chatRecyclerView.adapter = adapter
        adapter!!.startListening()
        adapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessageToUser(message: String) {
        binding.messageEdittext.setText("")
        chatroomModel?.apply {
            lastMessageSenderId = FirebaseUtil.currentUserID()
            timestamp = Timestamp.now()
            lastMessage = message

            FirebaseUtil.getChatroomReference(chatroomId).set(this)

            val chatMessageModel =
                ChatMessageModel(message, FirebaseUtil.currentUserID(), Timestamp.now())

            FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
        }
    }

    private fun getOrCreateChatRoomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result != null && result.exists()) {
                    // Chat room exists, retrieve it
                    chatroomModel = result.toObject(ChatroomModel::class.java)
                } else {
                    // Chat room doesn't exist, create a new one
                    chatroomModel = ChatroomModel(
                        chatroomId,
                        listOf(FirebaseUtil.currentUserID(), model.userId),
                        Timestamp.now(),
                        "", ""
                    )
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
                }
            }
        }
    }

}