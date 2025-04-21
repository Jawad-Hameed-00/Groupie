package com.jawadhameed.groupie

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.storage
import com.jawadhameed.groupie.databinding.ActivityUsernameBinding
import com.jawadhameed.groupie.models.UserModel
import com.jawadhameed.groupie.utils.FirebaseUtil

class UsernameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsernameBinding
    private val storage = Firebase.storage
    private val imagesRef = storage.reference.child("images")
    private var userModel: UserModel? = null
    private var profilePhoto: String = ""
    private var phoneNumber: String? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    binding.profilePhoto.setImageURI(it)
                    uploadImageToFirebaseStorage(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phoneNumber")

        binding.profilePhoto.setOnClickListener {
            openImageChooser()
        }

        binding.saveUserButton.setOnClickListener {
            setUserName()
        }

        getUserName()
    }

    private fun setUserName() {
        val userName = binding.userName.text.toString().trim()
        if (userName.length < 3) {
            binding.userName.error = "Username must be at least 3 characters"
            return
        }

        showLoading(true)

        if (userModel != null) {
            userModel!!.userName = userName
        } else {
            userModel = UserModel(
                FirebaseUtil.currentUserID(),
                phoneNumber ?: "",
                userName,
                profilePhoto,
                Timestamp.now()
            )
        }

        FirebaseUtil.currentUserDetails().set(userModel!!)
            .addOnCompleteListener {
                showLoading(false)
                if (it.isSuccessful) {
                    startActivity(Intent(this@UsernameActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Error saving user info", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserName() {
        showLoading(true)
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener {
            showLoading(false)
            if (it.isSuccessful) {
                userModel = it.result.toObject(UserModel::class.java)
                userModel?.let { user ->
                    binding.userName.setText(user.userName)
                    Glide.with(this)
                        .load(user.profilePhoto)
                        .placeholder(R.drawable.person)
                        .into(binding.profilePhoto)
                }
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImage.launch(intent)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        showLoading(true)

        val imageRef = imagesRef.child("${System.currentTimeMillis()}_image")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            showLoading(false)
            if (task.isSuccessful) {
                profilePhoto = task.result.toString()
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        binding.saveUserButton.isEnabled = !show
    }
}
