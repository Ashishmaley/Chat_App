package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import com.google.firebase.database.core.Tag
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Calendar
import java.util.Date
import java.util.logging.Handler

@Suppress("DEPRECATION")
class ChatActivity : AppCompatActivity() {
    private var binding : ActivityChatBinding? = null
    private var adapter : MessageAdapter?=null
    var message : ArrayList<Message>?=null
    var receiverRoom : String? = null
    var senderUid : String? = null
    var senderRoom : String? = null
    private var senderMessages: ArrayList<Message> = ArrayList()
    private var receiverMessages: ArrayList<Message> = ArrayList()
    var receiverUid : String? = null
    private var database :FirebaseDatabase? = null
    private var auth : FirebaseAuth? = null
    var storage : FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        message = ArrayList()

        val name = intent.getStringExtra("name")
        val profileImage = intent.getStringExtra("imgUri")

        binding!!.userName.text = name.toString()
        Picasso.get().load(profileImage).into(binding!!.imageProfile)

        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().uid

//            database!!.reference.child("users").child(receiverUid!!)
//                .addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                sImage = snapshot.child("profileImage").value.toString()
//                rImage = profileImage
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@ChatActivity,"error please check internet",Toast.LENGTH_LONG).show()
//            }
//        })

        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        adapter = MessageAdapter(this@ChatActivity,message,senderRoom!!,receiverRoom!!)

        binding!!.chatRecycleView.layoutManager = LinearLayoutManager(this)

        binding!!.chatRecycleView.adapter = adapter
        database!!.reference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    senderMessages.clear() // Clear the sender messages list before adding new messages
                    for (snap1 in snapshot.children) {
                        val msg: Message? = snap1.getValue(Message::class.java)
                        msg!!.messageId = snap1.key
                        senderMessages.add(msg)
                    }
                    updateAdapterWithCombinedMessages()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        database!!.reference.child("chats").child(receiverRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    receiverMessages.clear() // Clear the receiver messages list before adding new messages
                    for (snap1 in snapshot.children) {
                        val msg: Message? = snap1.getValue(Message::class.java)
                        msg!!.messageId = snap1.key
                        receiverMessages.add(msg)
                    }
                    updateAdapterWithCombinedMessages()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })

        binding!!.sendButton.setOnClickListener {
            val boxMessage = binding!!.inputMessage.text.toString()
            if (boxMessage.isNotEmpty()) {
                val date = Date()
                val msg = Message(boxMessage,senderUid, date.time)
                binding!!.inputMessage.setText("")

                val randomKey = database!!.reference.push().key
                val lastMessage =  HashMap <String,Any>()
                lastMessage["lastMsg"] = msg.message!!
                lastMessage["lastMsgTime"] = date.time

                database!!.reference.child("chats").child(senderRoom!!)
                    .updateChildren(lastMessage)
                database!!.reference.child("chats").child(receiverRoom!!)
                    .updateChildren(lastMessage)
                database!!.reference.child("chats").child(senderRoom!!)
                    .child("messages").child(randomKey!!).setValue(msg).addOnSuccessListener {
                        database!!.reference
                            .child("chats")
                            .child(receiverRoom!!)
                            .child("message")
                            .child(randomKey)
                            .setValue(msg)
                            .addOnSuccessListener {
                                for (msg in message!!) {
                                    Log.d("Message", "Message: ${msg.message}, Sender: ${msg.senderId}, Time: ${msg.timeStamp}")
                                }
                            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 25) {
            if (data != null) {
                if (data.data != null) {
                    val selectImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference =
                        storage!!.reference.child("chats").child(calendar.timeInMillis.toString() + "")
                    reference.putFile(selectImage!!).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val filepath = uri.toString()
                                val msgTxt = binding!!.inputMessage.text.toString()
                                val date = Date()
                                val msg = Message(msgTxt, senderUid, date.time)
                                msg.message = "photo"
                                msg.imageUrl = filepath
                                binding!!.inputMessage.setText("")

                                val randomKey = database!!.reference.push().key
                                val lastMessage = HashMap<String, Any>()
                                lastMessage["lastMsg"] = msg.message!!
                                lastMessage["lastMsgTime"] = date.time
                                database!!.reference.child("chats").child(senderRoom!!)
                                    .updateChildren(lastMessage)
                                database!!.reference.child("chats").child(receiverRoom!!)
                                    .updateChildren(lastMessage)
                                database!!.reference.child("chats").child(senderRoom!!)
                                    .child("messages").child(randomKey!!).setValue(msg)
                                    .addOnSuccessListener {
                                        database!!.reference.child("chats")
                                            .child(receiverRoom!!)
                                            .child("message")
                                            .child(randomKey)
                                            .setValue(msg)
                                            .addOnSuccessListener {
                                            }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun updateAdapterWithCombinedMessages() {
        message!!.clear() // Clear the combined message list before adding both sender and receiver messages
        message!!.addAll(senderMessages)
        message!!.addAll(receiverMessages)
        // Sort the messages based on the timestamp (if necessary)
        message!!.sortBy { it.timeStamp }
        adapter!!.notifyDataSetChanged()
    }


}//class