package com.example.dormitorymanager.View.StudentManagerAdmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.RegisterRoomManagerAdmin.RegisterRMFragment
import com.example.dormitorymanager.View.rvInter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.layout_item_room.view.img
import kotlinx.android.synthetic.main.layout_item_room.view.tvName
import kotlinx.android.synthetic.main.layout_item_student.view.*

class AdapterStudent(
    var list: MutableList<StudentInfor>,
    val onClickStudent: rvInter,
    val context: StudentFragment
    ) : ListAdapter<StudentInfor, AdapterStudent.StudentViewHolder>(StudentDiffCallback()) {


        inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_item_student, parent, false)

            return StudentViewHolder(view)

        }

        fun setData(student: MutableList<StudentInfor>) {
            list = student.toMutableList()
            submitList(list)
        }

        override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {

            holder.itemView.apply {
                tvName.text = list[position].fullname
                tvIdStd.text = list[position].idStudent
                tvclass.text = list[position].classStd
                tvphone.text = list[position].phone
                val avatar = list[position].avatar
                Glide.with(context)
                    .load(avatar)
                    .placeholder(R.drawable.ic_person) // Ảnh hiển thị khi đang load ảnh từ Storage
                    .error(R.drawable.ic_person) // Ảnh hiển thị khi load ảnh từ Storage thất bại
                    .into(holder.itemView.findViewById(R.id.img))

                //itemclick chọn
                holder.itemView.setOnClickListener {
                    onClickStudent.onClickStudent(position)
                }
                holder.itemView.isFocusable = true
                holder.itemView.isLongClickable = true

                holder.itemView.setOnLongClickListener { view ->
                    // Lấy vị trí của ViewHolder trong Adapter
                    val position = holder.adapterPosition
                    onClickStudent.onItemLongClick(position)
                    return@setOnLongClickListener true
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private class StudentDiffCallback : DiffUtil.ItemCallback<StudentInfor>() {
        override fun areItemsTheSame(oldItem: StudentInfor, newItem: StudentInfor): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: StudentInfor, newItem: StudentInfor): Boolean {
            TODO("Not yet implemented")
            return oldItem._id == newItem._id
        }
    }
