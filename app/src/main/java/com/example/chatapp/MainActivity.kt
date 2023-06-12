package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.helper.widget.Carousel.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Data.User
import com.example.chatapp.LoginOtpProfile.ProfileActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private var auth : FirebaseAuth? = null
    private var userAdapter : UserAdapter?=null
    private var database : FirebaseDatabase?=null
    private var userList : ArrayList<User>?=null
    private var user:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        userList = ArrayList()

        val recyclerView=binding!!.recycleView
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter  = UserAdapter(this, userList!!)
//        database!!.reference.child("users")
//            .child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    user=snapshot.getValue(User :: class.java)
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//
//            })
        binding!!.recycleView.adapter=userAdapter
        database!!.reference.child("users").addValueEventListener(object : ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList!!.clear()
                for (snap in snapshot.children)
                {
                    val user:User?=snap.getValue(User ::class.java)
//                    if (user!!.uid!! != FirebaseAuth.getInstance().uid)
                    if (user != null) {
                        userList!!.add(user)
                    }
                }
                userAdapter!!.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}

        })


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profileUpdate -> {
                profileUpdate(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun profileUpdate(item: MenuItem) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}