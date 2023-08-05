package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapters.MessageAdapter
import com.example.chatapp.Data.Message
import com.example.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.Date

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {
    private var binding : ActivityChatBinding? = null
    private var adapter : MessageAdapter?=null
    var message : ArrayList<Message>?=null
    var receiverRoom : String? = null
    var senderUid : String? = null
    var senderRoom : String? = null
    var receiverUid : String? = null
    private var database :FirebaseDatabase? = null
    private var auth : FirebaseAuth? = null
    var rImage : String? = null
    var sImage : String? = null
    var textMessage : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        message = ArrayList()

        val name = intent.getStringExtra("name")
        val profileImage = intent.getStringExtra("imgUri")

        receiverUid = intent.getStringExtra("uid")
        senderUid = auth!!.uid

        binding!!.userName.text = name.toString()
        Picasso.get().load(profileImage).into(binding!!.imageProfile)


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

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        adapter = MessageAdapter(this@ChatActivity,message,senderRoom!!,receiverRoom!!)

        binding!!.chatRecycleView.layoutManager = LinearLayoutManager(this)
        binding!!.chatRecycleView.adapter = adapter
        database!!.reference.child("chats").child(senderRoom!!).child("message")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   message!!.clear()
                    for(snap1 in snapshot.children){
                        val msg : Message ? =  snap1.getValue(Message::class.java)
                        msg!!.messageId=snap1.key
                        message!!.add(msg)
                    }
                    adapter!!.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {

                }

            })

        binding!!.sendButton.setOnClickListener {
            textMessage = binding!!.inputMessage.text.toString()
            if (textMessage!!.isNotEmpty()) {
                val date = Date()
                val msg = Message(textMessage,senderUid, date.time)
                binding!!.inputMessage.setText("")

                var randomKey = database!!.reference.push().key
                val lastMessage =  HashMap <String,Any>()
                lastMessage["lastMsg"] = msg.message!!
                lastMessage["lastMsgTime"] = date.time

                database!!.reference.child("chat").child(senderRoom!!)
                    .updateChildren(lastMessage)
                database!!.reference.child("chat").child(receiverRoom!!)
                    .updateChildren(lastMessage)
                database!!.reference.child("chat").child(senderRoom!!)
                    .child("messages").child(randomKey!!).setValue(msg).addOnSuccessListener {
                        database!!.reference.child("chat").child(receiverRoom!!)
                            .child("message").child(randomKey).setValue(msg).addOnSuccessListener {  }


                    }

            } else {
                Toast.makeText(this@ChatActivity, "Type message first", Toast.LENGTH_LONG).show()
            }
        }
        binding!!.attachButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }


    }//onCreate


}//class