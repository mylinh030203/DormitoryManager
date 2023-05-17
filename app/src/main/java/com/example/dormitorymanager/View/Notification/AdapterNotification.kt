package com.example.dormitorymanager.View.Notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Notification
import com.example.dormitorymanager.R

class AdapterNotification (private val notifications: List<Notification>) :
    RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_notification,
            parent,
            false
        )
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int = notifications.size

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textMessage: TextView = itemView.findViewById(R.id.textMessage)

        fun bind(notification: Notification) {
            textTitle.text = notification.title
            textMessage.text = notification.message
            // Gắn dữ liệu cho các phần tử khác (nếu có)
        }
    }
}