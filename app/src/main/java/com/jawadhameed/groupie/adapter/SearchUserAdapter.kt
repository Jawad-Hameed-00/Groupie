package com.jawadhameed.groupie.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jawadhameed.groupie.ChatActivity
import com.jawadhameed.groupie.R
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.AndroidUtil
import com.jawadhameed.groupie.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView

class SearchUserAdapter(
    private val options: FirestoreRecyclerOptions<UserModel>,
    private val context: Activity
) : FirestoreRecyclerAdapter<UserModel, SearchUserAdapter.SearchViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int, model: UserModel) {
        holder.userName.text = model.userName
        holder.phoneNumber.text = model.phoneNumber
        Glide.with(context as Context).load(model.profilePhoto).placeholder(R.drawable.person).into(holder.profilePhoto)
        class MainActivity : AppCompatActivity() {
            private lateinit var myTextView: TextView

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
                myTextView.text = "Hello!" // CRASH: myTextView is null
            }
        }
        if (model.userId == FirebaseUtil.currentUserID()) {
            holder.userName.text = "${model.userName} (Me)"
        }

        holder.itemView.setOnClickListener {
            closeKeyboard()
            val intent = Intent(context, ChatActivity::class.java)
            AndroidUtil.passUserModelAsIntent(intent, model)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            context.finish()
        }
    }

    private fun closeKeyboard() {
        val view = context.currentFocus
        view?.let {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userNameText)
        val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumberText)
        val profilePhoto: CircleImageView = itemView.findViewById(R.id.circleImageView)
    }
}
