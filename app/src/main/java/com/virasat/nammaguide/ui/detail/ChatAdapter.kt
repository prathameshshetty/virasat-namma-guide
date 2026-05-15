package com.virasat.nammaguide.ui.detail

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.virasat.nammaguide.databinding.ItemChatMessageBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.VH>() {
    private val messages = mutableListOf<ChatMessage>()

    inner class VH(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val msg = messages[position]
        holder.binding.tvMessage.text = msg.text
        val params = holder.binding.tvMessage.layoutParams as LinearLayout.LayoutParams
        if (msg.isUser) {
            params.gravity = Gravity.END
            holder.binding.tvMessage.setBackgroundResource(com.virasat.nammaguide.R.drawable.bg_chat_user)
        } else {
            params.gravity = Gravity.START
            holder.binding.tvMessage.setBackgroundResource(com.virasat.nammaguide.R.drawable.bg_chat_ai)
        }
        holder.binding.tvMessage.layoutParams = params
    }

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }
}
