package com.example.chatapp.LoginOtpProfile

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private var binding : ActivityLoginBinding? = null
    private var auth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = null
    private var number:String? = ""
    private var progressBar : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        val getOtp = binding!!.getOtp


        getOtp.setOnClickListener {
            progressBar = binding!!.progressBar
            progressBar!!.visibility=View.VISIBLE
            val n = binding!!.Number
            number = n.text.toString()
            val option = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(number!!)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Verification completed",
                            Toast.LENGTH_SHORT
                        ).show()

                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        progressBar!!.visibility=View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "Verification failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid request",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Invalid request
                            }
                            is FirebaseTooManyRequestsException -> {
                                // The SMS quota for the project has been exceeded
                                Toast.makeText(
                                    this@LoginActivity,
                                    " The SMS quota has been exceeded",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            is FirebaseAuthMissingActivityForRecaptchaException -> {
                                // reCAPTCHA verification attempted with null Activity
                                Toast.makeText(
                                    this@LoginActivity,
                                    "reCAPTCHA verification attempted with null Activity",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Toast.makeText(this@LoginActivity, "Code sent", Toast.LENGTH_SHORT).show()
                       // Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
                        val otpIntent = Intent(this@LoginActivity, OtpVerification::class.java)
                        otpIntent.putExtra("otpCode",verificationId)
                        progressBar!!.visibility=View.GONE
                        startActivity(otpIntent)
                        finish()
                        // Save verification ID and resending token so we can use them later

                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(option)

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    sendHome()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Sign In Failed",Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility=View.GONE
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    //sendHome()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (currentUser!=null)
        {
            sendHome()
        }
    }
    private fun sendHome() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the OTPVerification activity after starting MainActivity
    }

}