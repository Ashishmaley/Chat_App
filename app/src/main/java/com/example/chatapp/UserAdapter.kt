package com.example.chatapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.chatapp.Data.User
import com.example.chatapp.databinding.ItemBinding
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context,val userArray: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
       val create = LayoutInflater.from(context).inflate(R.layout.item,parent,false)
    return ViewHolder(create)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = userArray[position]
        holder.binding.name.text = user.name
        holder.binding.number.text = user.phoneNumber
        Picasso.get().load(user.profileImage).into(holder.binding.userImage)
    }

    override fun getItemCount(): Int = userArray.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : ItemBinding = ItemBinding.bind(itemView)
    }


}