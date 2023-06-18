package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private var binding : ActivityChatBinding? = null
    private var database :FirebaseDatabase? = null
    private var auth : FirebaseAuth? = null
    var rImage : String? = null
    var sImage : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val name = intent.getStringExtra("name")
        val profileImage = intent.getStringExtra("imgUri")
        binding!!.userName.text = name.toString()
        Picasso.get().load(profileImage).into(binding!!.imageProfile)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val reference: DatabaseReference? =auth!!.uid?.let { database!!.reference.child("users").child(it) }
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                sImage = snapshot.child("profileImage").value.toString()
                rImage = profileImage
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity,"error please check internet",Toast.LENGTH_LONG).show()
            }
        })
    }
}