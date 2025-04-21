package com.jawadhameed.groupie

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.jawadhameed.groupie.databinding.ActivityCodeBinding
import `in`.aabhasjindal.otptextview.OTPListener
import java.util.concurrent.TimeUnit

class CodeActivity : AppCompatActivity() {

    private var phoneNumber: String? = null
    private lateinit var binding: ActivityCodeBinding
    private var progressDialog: AlertDialog? = null
    private lateinit var auth: FirebaseAuth
    private var verificationId2 = ""
    private val timerOut = 60L
    private var resendTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phoneNumber")
        auth = FirebaseAuth.getInstance()

        binding.submitOTPButton.isEnabled = false
        binding.phoneText.text = "Enter OTP sent to your phone number \n$phoneNumber"

        showProgressDialog("Sending OTP...")

        PhoneAuthProvider.verifyPhoneNumber(setupPhoneAuthOptions(phoneNumber!!))

        binding.resendOTP.setOnClickListener {
            resendVerificationCode(phoneNumber!!)
        }

        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.submitOTPButton.isEnabled = false
            }

            override fun onOTPComplete(otp: String) {
                binding.submitOTPButton.isEnabled = true
                verifyPhoneNumberWithCode(verificationId2, otp)
            }
        }
    }

    private fun setupPhoneAuthOptions(phoneNumber: String): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timerOut, TimeUnit.SECONDS)
            .setActivity(this@CodeActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    dismissProgressDialog()
                    Toast.makeText(this@CodeActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    dismissProgressDialog()
                    verificationId2 = verificationId
                    resendCodeTimer()
                    Toast.makeText(this@CodeActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            }).build()
    }

    private fun resendVerificationCode(phoneNumber: String) {
        showProgressDialog("Resending OTP...")
        PhoneAuthProvider.verifyPhoneNumber(setupPhoneAuthOptions(phoneNumber))
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        showProgressDialog("Verifying OTP...")
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this@CodeActivity, "Verification ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@CodeActivity) { task ->
                dismissProgressDialog()
                if (task.isSuccessful) {
                    val intent = Intent(this@CodeActivity, UsernameActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CodeActivity, "Invalid OTP Code", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showProgressDialog(message: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        val messageText = view.findViewById<TextView>(R.id.progressMessage)
        messageText.text = message

        progressDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun resendCodeTimer() {
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOTP.isEnabled = false
                val secondsLeft = millisUntilFinished / 1000
                binding.resendOTP.text = "Resend OTP in $secondsLeft seconds"
            }

            override fun onFinish() {
                binding.resendOTP.isEnabled = true
                binding.resendOTP.text = "Resend OTP"
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        resendTimer?.cancel()
        dismissProgressDialog()
    }
}
