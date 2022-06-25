package com.example.simple_message.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.R
import com.example.simple_message.factories.Chat
class feedAdapter(private var tags: Array<Chat?>) : RecyclerView
.Adapter<feedAdapter.viewHolder>() {

    class viewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.chatTag)
        val name: TextView = itemView.findViewById(R.id.name)
        var uid: String? = null
        init {
            itemView.setOnClickListener {
                listener.OnClick(adapterPosition)
            }
        }
    }

    private lateinit var onCLickListener: OnItemClickListener

    interface OnItemClickListener {

        fun OnClick(position: Int) {
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        onCLickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_layout, parent, false)
        return viewHolder(itemView, onCLickListener)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val chat = tags[position]
        holder.textView.text = chat?.text
        holder.name.text = chat?.name
        holder.uid = chat?.uid
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount() = tags.size

    fun updateTags(tags: Array<Chat?>) {
        this.tags = tags
    }
}
