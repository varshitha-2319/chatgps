package com.example.compose.jetchat.conversation.newchat

import android.graphics.Color  // Import for text color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.compose.jetchat.R

import androidx.recyclerview.widget.RecyclerView
import com.example.compose.jetchat.databinding.ItemMessageBinding
import com.bumptech.glide.Glide // Import for profile images

class MessageAdapter(private val messages: MutableList<NewChatMessage>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messages: NewChatMessage) {
            binding.senderName.text = messages.author
            binding.messageBody.text = messages.content
            binding.timestamp.text = messages.timestamp


            binding.senderName.setTextColor(Color.BLACK)
            binding.messageBody.setTextColor(Color.BLACK)
            binding.timestamp.setTextColor(Color.DKGRAY) // Use dark gray for a subtle look


          }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size
}
