package com.example.chatapp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatActivity
import com.example.chatapp.Data.User
import com.example.chatapp.R
import com.example.chatapp.databinding.ItemBinding
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context, private val userArray: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemBinding = ItemBinding.bind(itemView)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val create = LayoutInflater.from(context).inflate(R.layout.item,parent,false)
    return ViewHolder(create)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userArray[position]
        holder.binding.name.text = user.name
        holder.binding.number.text = user.phoneNumber
        Picasso.get().load(user.profileImage).into(holder.binding.userImage)
        holder.itemView.setOnClickListener {
        var intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("name", user.name)
            putExtra("imgUri", user.profileImage)
            putExtra("uid", user.uid)
        }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = userArray.size
}
