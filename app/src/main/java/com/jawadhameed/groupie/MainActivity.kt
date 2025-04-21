package com.jawadhameed.groupie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.jawadhameed.groupie.adapter.RecentChatAdapter
import com.jawadhameed.groupie.databinding.ActivityMainBinding
import com.jawadhameed.groupie.models.ChatroomModel
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.FirebaseUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userModel: UserModel? = null
    private var adapter: RecentChatAdapter? = null
    private var chatroomList: ArrayList<ChatroomModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseUtil.currentUserID())
            .addSnapshotListener { value, error ->
                if (error == null) {
                    userModel = value?.toObject(UserModel::class.java)
                }
            }


        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        adapter = RecentChatAdapter(emptyList(), this)
        binding.recentChatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recentChatRecyclerView.adapter = adapter

        FirebaseFirestore.getInstance().collection("Chatrooms").addSnapshotListener { value, error ->

            // Clear the list before adding new data
            chatroomList.clear()

            // Loop through the documents in the snapshot
            value?.documents?.forEach { document ->
                val chatroom = document.toObject(ChatroomModel::class.java)
                chatroom?.let {
                    chatroomList.add(it)
                }
            }

            adapter?.chatroomList = chatroomList
            adapter?.updateData(chatroomList)
        }
    }
}
