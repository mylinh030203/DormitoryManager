package com.example.dormitorymanager.View.Notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Notification
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.rvInter

class AdapterNotification (var notifications: MutableList<Notification>,
                           val onclickNotification:rvInter,
                           val context : ListNotificationFragment
                           ) : ListAdapter<Notification, AdapterNotification.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_notification,
            parent,
            false
        )
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.itemView.apply {
            val notification = notifications[position]
            holder.bind(notification)
            //itemclick chọn
            holder.itemView.setOnClickListener {
                onclickNotification.onClickNotification(position)
            }
            holder.itemView.isFocusable = true
            holder.itemView.isLongClickable = true

            holder.itemView.setOnLongClickListener { view ->
                // Lấy vị trí của ViewHolder trong Adapter
                val position = holder.adapterPosition
                onclickNotification.onItemLongClick(position)
                return@setOnLongClickListener true
            }
        }

    }
    fun setData(notification: MutableList<Notification>) {
        notifications = notification.toMutableList()
        submitList(notifications)
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
private class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}