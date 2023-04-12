package com.example.dormitorymanager.View.Student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.rvInter
import kotlinx.android.synthetic.main.layout_item_room.view.*
import kotlinx.android.synthetic.main.layout_item_room.view.img
import kotlinx.android.synthetic.main.layout_item_room.view.tvName
import kotlinx.android.synthetic.main.layout_item_student.view.*

class AdapterStudent (
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
                img.setImageResource(R.drawable.phong)

                //itemclick chọn
                holder.itemView.setOnClickListener {
                    onClickStudent.onClickStudent(position)
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
