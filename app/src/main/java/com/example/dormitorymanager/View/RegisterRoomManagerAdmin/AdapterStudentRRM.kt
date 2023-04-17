package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.StudentManagerAdmin.AdapterStudent
import com.example.dormitorymanager.View.rvInter
import kotlinx.android.synthetic.main.layout_item_room.view.*
import kotlinx.android.synthetic.main.layout_item_student.view.*
import kotlinx.android.synthetic.main.layout_item_student.view.tvIdStd
import kotlinx.android.synthetic.main.layout_item_student.view.tvclass
import kotlinx.android.synthetic.main.layout_item_student.view.tvphone
import kotlinx.android.synthetic.main.layout_item_student_rrm.view.*

class AdapterStudentRRM
    (
    var list: MutableList<StudentInfor>,
    val onClickStudent: rvInter,
    val context: RegisterRMFragment
) : ListAdapter<StudentInfor, AdapterStudentRRM.StudentViewHolder>(StudentDiffCallback()) {


    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_student_rrm, parent, false)

        return StudentViewHolder(view)

    }

    fun setData(student: MutableList<StudentInfor>) {
        list = student.toMutableList()
        submitList(list)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.itemView.apply {
            tvNamerr.text = list[position].fullname
            tvIdStdrr.text = list[position].idStudent
            tvclassrr.text = list[position].classStd
            tvphonerr.text = list[position].phone
            imgrr.setImageResource(R.drawable.phong)

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
