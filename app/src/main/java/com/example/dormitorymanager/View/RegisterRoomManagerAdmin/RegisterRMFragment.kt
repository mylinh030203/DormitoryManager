package com.example.dormitorymanager.View.RegisterRoomManagerAdmin

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
import com.example.dormitorymanager.View.StudentManagerAdmin.AdapterStudent
import com.example.dormitorymanager.View.rvInter
import com.example.dormitorymanager.ViewModel.ViewModelDetailRR
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser

class RegisterRMFragment : Fragment() {
    private lateinit var adapter: AdapterStudentRRM
    private lateinit var rvStudent: RecyclerView
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var viewModelDerailRR: ViewModelDetailRR
    private var longClickedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelDerailRR = ViewModelProvider(this).get(ViewModelDetailRR::class.java)
        val view = inflater.inflate(R.layout.fragment_register_r_m, container, false)
        var btnaddSt = view.findViewById<Button>(R.id.btnAddStudent)
        var roomID = arguments?.getString("id", "8").toString()
        rvStudent = view.findViewById(R.id.rvStudent)
        var bundle = Bundle()
        bundle.putString("_id", roomID)
        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                selectStudent(viewModelStudent.getStudentInRoom(roomID))
                // Inflate the layout for this fragment
                btnaddSt.setOnClickListener {
                    val navController = view?.findNavController()
                    navController?.navigate(
                        R.id.action_registerRMFragment_to_addStudentInRoomFragment,
                        bundle
                    )
                }
            }else{
                selectStudent(viewModelStudent.getStudentInRoom(roomID))
                btnaddSt.visibility = View.GONE
            }
        }
        return view
    }

    fun selectStudent(list: MutableList<StudentInfor>) {
        adapter = AdapterStudentRRM(list, object : rvInter {
            override fun onClickStudent(position: Int) {
                val bundle = Bundle()
                bundle.putString("id", adapter.currentList[position]._id)
                bundle.putString("fullname", adapter.currentList[position].fullname)
                bundle.putString("phone", adapter.currentList[position].phone)
                bundle.putString("gender", adapter.currentList[position].gender)
                bundle.putString("idStudent", adapter.currentList[position].idStudent)
                bundle.putString("classStd", adapter.currentList[position].classStd)
                bundle.putString("avatar", adapter.currentList[position].avatar)
//                val navController = view?.findNavController()
//                navController?.navigate(
//                    R.id.action_studentFragment_to_updateStudentFragment,
//                    bundle
//                )

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
        viewModelStudent.studentByRoomID.observe(viewLifecycleOwner, { students ->
            adapter.setData(students.toMutableList())
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(100, 11, 1, "Delete register")
        menu?.add(100, 12, 2, "Detail register room")
        val position = longClickedPosition
        menu?.setHeaderTitle("Student ${adapter.currentList[position].fullname}")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            11 -> {
                // Lấy vị trí của item được long click
                val position = longClickedPosition
                if (position != -1) {
                    viewModelDerailRR.deleteDetail(adapter.currentList[position]._id)
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
                val position = longClickedPosition
                if (position != -1) {

                    val detailId = adapter.currentList[position]._id
                    if (detailId != null) {
                        viewModelDerailRR.getDeltail(detailId)
                    }

                    viewModelDerailRR.detailRR.observe(viewLifecycleOwner) { detail ->
                        if (detail != null) {
                            // Cập nhật giao diện tương ứng với giá trị của đối tượng DetailRoomRegister
                            val bundle = Bundle()
                            var roomID = arguments?.getString("id", "8").toString()
                            var roomName = arguments?.getString("name", "8").toString()
                            bundle.putString("id", adapter.currentList[position]._id)
                            bundle.putString("fullname", adapter.currentList[position].fullname)
                            bundle.putString("room_id", roomID)
                            bundle.putString("room_name", roomName)
                            bundle.putString("registrationDate", detail.registerDate.toString())
                            bundle.putString("expirationDate", detail.expirationDate.toString())
                            bundle.putString("status",detail.status.toString())
                            bundle.putString("price", detail.price.toString())
                            // và các giá trị khác tương ứng
                            val navController = view?.findNavController()
                            navController?.navigate(
                                R.id.action_registerRMFragment_to_updateStudentInRoomFragment,
                                bundle
                            )
                        }
                    }
                    longClickedPosition = -1
                }

            }
        }
        return super.onContextItemSelected(item)
    }


}


