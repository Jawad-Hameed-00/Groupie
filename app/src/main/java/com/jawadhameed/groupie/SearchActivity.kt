package com.jawadhameed.groupie

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jawadhameed.groupie.adapter.SearchUserAdapter
import com.jawadhameed.groupie.databinding.ActivitySearchBinding
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.FirebaseUtil

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var adapter: SearchUserAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            closeKeyboard()
            finish()
        }

        binding.searchButton.setOnClickListener {
            val searchText = binding.searchEditText.text.toString().trim()
            if (searchText.length > 3) {
                filterList(searchText)
            }
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = binding.searchEditText.text.toString().trim()
                if (searchText.length > 3) {
                    filterList(searchText)
                }
                closeKeyboard()
                true
            } else false
        }

        binding.searchEditText.requestFocus()
        showKeyboard()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.windowToken?.let {
            imm.hideSoftInputFromWindow(it, 0)
        }
    }

    private fun filterList(searchText: String) {
        val query = FirebaseUtil.allUserCollectionReference()
            .orderBy("userName")
            .startAt(searchText)
            .endAt(searchText + "\uf8ff")

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java)
            .build()

        adapter?.stopListening()
        adapter = SearchUserAdapter(options, this)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.adapter = adapter
        adapter?.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}
