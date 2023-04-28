package com.example.dormitorymanager.View.StudentManagerAdmin

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.rvInter
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser

class StudentFragment : Fragment() {
    private lateinit var adapter: AdapterStudent
    private lateinit var rvStudent: RecyclerView
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var viewModelDetailRR: ViewModelDetailRR
    private lateinit var viewModelRoom: ViewModelRoom
    private var longClickedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelDetailRR = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        var view: View = inflater.inflate(R.layout.fragment_student, container, false)
        var btnaddSt = view.findViewById<Button>(R.id.btnAddStudent)
        var btnsearch = view.findViewById<Button>(R.id.btnSearch)
        rvStudent = view.findViewById(R.id.rvStudent)
        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                selectStudent(viewModelStudent.getStudent())
                // Inflate the layout for this fragment
                btnaddSt.setOnClickListener {
                    val navController = view?.findNavController()
                    navController?.navigate(R.id.action_studentFragment_to_addStudentFragment)
                }

                btnsearch.setOnClickListener {
                    selectStudentByStutus(viewModelStudent.getStudentUnapproved())
                }

            }
        }
        return view
    }

    fun selectStudent(list: MutableList<StudentInfor>) {
        adapter = AdapterStudent(list, object : rvInter {
            override fun onClickStudent(position: Int) {
                val bundle = Bundle()
                bundle.putString("id", adapter.currentList[position]._id)
                bundle.putString("fullname", adapter.currentList[position].fullname)
                bundle.putString("phone", adapter.currentList[position].phone)
                bundle.putString("gender", adapter.currentList[position].gender)
                bundle.putString("idStudent", adapter.currentList[position].idStudent)
                bundle.putString("classStd", adapter.currentList[position].classStd)
                bundle.putString("avatar", adapter.currentList[position].avatar)
                val navController = view?.findNavController()
                navController?.navigate(
                    R.id.action_studentFragment_to_updateStudentFragment,
                    bundle
                )

            }

            override fun onItemLongClick(position: Int) {
                // Lưu trữ vị trí của item được long click
                longClickedPosition = position

                // Hiển thị context menu với vị trí position của item trong RecyclerView

                registerForContextMenu(rvStudent)
                rvStudent.showContextMenuForChild(rvStudent.getChildAt(position))
                unregisterForContextMenu(rvStudent)
            }
        }, this)
        rvStudent.adapter = adapter
        rvStudent.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.HORIZONTAL,
            false
        )
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelStudent.students.observe(viewLifecycleOwner, { students ->
            adapter.setData(students.toMutableList())
        })

    }

    fun selectStudentByStutus(list: MutableList<StudentInfor>) {
        adapter = AdapterStudent(list, object : rvInter {
            override fun onClickStudent(position: Int) {
                val detailId = adapter.currentList[position]._id
                if (detailId != null) {
                    viewModelDetailRR.getDeltail(detailId)
                }

                viewModelDetailRR.detailRR.observe(viewLifecycleOwner) { detail ->
                    if (detail != null) {
                        // Cập nhật giao diện tương ứng với giá trị của đối tượng DetailRoomRegister
                        val bundle = Bundle()
                        bundle.putString("id", adapter.currentList[position]._id)
                        bundle.putString("fullname", adapter.currentList[position].fullname)
                        bundle.putString("room_id", detail.room_id.toString())
                        viewModelRoom.getRoomName(adapter.currentList[position]._id){
                                roomName->
                            bundle.putString("room_name", roomName)
                        }
                        bundle.putString("registrationDate", detail.registerDate.toString())
                        bundle.putString("expirationDate", detail.expirationDate.toString())
                        bundle.putString("status", detail.status.toString())
                        bundle.putString("price", detail.price.toString())
                        val navController = view?.findNavController()
                        navController?.navigate(
                            R.id.action_studentFragment_to_updateStudentInRoomFragment3,
                            bundle
                        )
                    }
                }
            }

            override fun onItemLongClick(position: Int) {
                // Lưu trữ vị trí của item được long click
                longClickedPosition = position

                // Hiển thị context menu với vị trí position của item trong RecyclerView

                registerForContextMenu(rvStudent)
                rvStudent.showContextMenuForChild(rvStudent.getChildAt(position))
                unregisterForContextMenu(rvStudent)
            }
        }, this)
        rvStudent.adapter = adapter
        rvStudent.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.HORIZONTAL,
            false
        )
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelStudent.StudentUnapproved.observe(viewLifecycleOwner, { students ->
            adapter.setData(students.toMutableList())
        })

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(100, 11, 1, "delete")
        menu?.add(100, 12, 2, "Detail")
        val position = longClickedPosition
        menu?.setHeaderTitle("Student ${adapter.currentList[position].fullname}")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            11 -> {
                // Lấy vị trí của item được long click
                val position = longClickedPosition
                if (position != -1) {
                    viewModelStudent.deleteStudent(adapter.currentList[position]._id)
                    longClickedPosition = -1 // Reset giá trị của biến tạm
                    var ad = AlertDialog.Builder(requireContext())
                    ad.setTitle("Delete record")
                    ad.setMessage("Delete success")
                    ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show()
                    }).show()
                    return true
                }


            }
            12 -> {

            }
        }
        return super.onContextItemSelected(item)
    }


}