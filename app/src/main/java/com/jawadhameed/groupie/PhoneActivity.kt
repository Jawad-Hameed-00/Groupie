package com.jawadhameed.groupie

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jawadhameed.groupie.databinding.ActivityPhoneBinding

class PhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ccp.registerCarrierNumberEditText(binding.phoneNumber)
        binding.sendOTPButton.setOnClickListener {
            if (binding.ccp.isValidFullNumber){
                val intent = Intent(this@PhoneActivity, CodeActivity::class.java)
                intent.putExtra("phoneNumber", binding.ccp.fullNumberWithPlus)
                startActivity(intent)
            }
        }

    }
}