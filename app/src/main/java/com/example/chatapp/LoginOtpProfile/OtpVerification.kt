package com.example.chatapp.LoginOtpProfile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.chatapp.databinding.ActivityOtpVerificationBinding
import com.google.firebase.auth.*

class OtpVerification : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding
    private var auth = FirebaseAuth.getInstance()
    private var phoneNumber:String? = null
    private var currentUser : FirebaseUser? = null
    private var progressBar : ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val verificationId = intent.getStringExtra("otpCode")
        phoneNumber = intent.getStringExtra("number")
        val register = binding.button
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val otpInputContainer = binding.otpInputContainer

            val otpDigits: Array<EditText> = arrayOf(
                binding.otpDigit1,
                binding.otpDigit2,
                binding.otpDigit3,
                binding.otpDigit4,
                binding.otpDigit5,
                binding.otpDigit6
            )

            for (i in otpDigits.indices) {
                otpDigits[i].addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s?.length == 1 && i < otpDigits.size - 1) {
                            otpDigits[i + 1].requestFocus()
                        } else if (s?.length == 0 && i > 0) {
                            otpDigits[i - 1].requestFocus()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }

        register.setOnClickListener {
            // Retrieve the entered OTP code
            progressBar = binding.progressBar
            progressBar!!.visibility=View.VISIBLE
            val otpCode = otpInputContainer.children.joinToString("") {
                (it as EditText).text.toString()
            }

            // Process the entered OTP code
            if (otpCode.isNotEmpty()) {
                val credential =verificationId?.let { it1 -> PhoneAuthProvider.getCredential(it1, otpCode) }
                if (credential != null) {
                    signInWithPhoneAuthCredential(credential)
                }
            } else {
                Toast.makeText(this, "Mismatch$verificationId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    sendHome()
//                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Toast.makeText(this,"Sign In Failed",Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility= View.GONE
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (currentUser != null) {
            sendHome()
        }
    }

    private fun sendHome() {
        val intent = Intent(this@OtpVerification, ProfileActivity::class.java)
        startActivity(intent).apply {
            intent.putExtra("number",phoneNumber)
        }
        finish() // Optional: Finish the OTPVerification activity after starting MainActivity
    }
}
