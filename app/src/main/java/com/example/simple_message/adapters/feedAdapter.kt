package com.example.simple_message.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.R

class feedAdapter(private val tags: Array<String?>) : RecyclerView
.Adapter<feedAdapter.viewHolder>() {

    class viewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.chatTag)
        init {
            itemView.setOnClickListener{
                listener.OnClick(adapterPosition)
            }
        }
    }

    private lateinit var onCLickListener: OnItemClickListener

    interface OnItemClickListener{

        fun OnClick(position: Int){

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
        holder.textView.text = tags[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount() = tags.size
}