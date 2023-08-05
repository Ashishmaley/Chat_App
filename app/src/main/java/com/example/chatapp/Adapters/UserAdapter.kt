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

class UserAdapter(val context: Context,val userArray: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val create = LayoutInflater.from(context).inflate(R.layout.item,parent,false)
    return ViewHolder(create,userArray)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userArray[position]
        holder.binding.name.text = user.name
        holder.binding.number.text = user.phoneNumber
        Picasso.get().load(user.profileImage).into(holder.binding.userImage)
    }

    override fun getItemCount(): Int = userArray.size

    class ViewHolder(itemView: View,userArray: ArrayList<User>) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemBinding = ItemBinding.bind(itemView)
        init {
            binding.itemLayout.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userArray[position]
                    startActivity(itemView.context, user)
                }
            }
        }

    }
}
fun startActivity(context: Context, user: User) {
    val intent = Intent(context, ChatActivity::class.java).apply {
        putExtra("name",user.name)
        putExtra("imgUri",user.profileImage)
        putExtra("uid",user.uid)
    } // Pass any necessary data to the ChatActivity
    context.startActivity(intent)
}