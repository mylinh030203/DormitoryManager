package com.example.dormitorymanager.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.RoomManagerAdmin.RoomFragment
import kotlinx.android.synthetic.main.layout_item_room.view.*

class AdapterRoom(
    var list: MutableList<Rooms>,
    val onClickRoom: rvInter,
    val context: RoomFragment
) : ListAdapter<Rooms, AdapterRoom.RoomViewHolder>(RoomDiffCallback()) {


    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_room, parent, false)

        return RoomViewHolder(view)

    }

    fun setData(rooms: MutableList<Rooms>) {
        list = rooms.toMutableList()
        submitList(list)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.itemView.apply {
            tvName.text = list[position].name
            tvDes.text = list[position].description
            tvStatus.text = list[position].status
            img.setImageResource(R.drawable.phong)

            //itemclick chọn
            holder.itemView.setOnClickListener {
                onClickRoom.onClickRoom(position)
            }
            holder.itemView.isFocusable = true
            holder.itemView.isLongClickable = true

            holder.itemView.setOnLongClickListener { view ->
                // Lấy vị trí của ViewHolder trong Adapter
                val position = holder.adapterPosition
                onClickRoom.onItemLongClick(position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

private class RoomDiffCallback : DiffUtil.ItemCallback<Rooms>() {
    override fun areItemsTheSame(oldItem: Rooms, newItem: Rooms): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Rooms, newItem: Rooms): Boolean {
        return oldItem == newItem
    }
}