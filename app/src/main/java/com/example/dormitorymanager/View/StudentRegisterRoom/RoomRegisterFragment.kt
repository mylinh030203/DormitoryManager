package com.example.dormitorymanager.View.StudentRegisterRoom

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
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.AdapterRoom
import com.example.dormitorymanager.View.rvInter
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.FragmentRoomRegisterBinding


class RoomRegisterFragment : Fragment() {
    private lateinit var binding: FragmentRoomRegisterBinding
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewModel: ViewModelUser
    private lateinit var adapter: AdapterRoomUser
    private lateinit var rvRoom: RecyclerView
    private var longClickedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        rvRoom = binding.rvRoomUser
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        // Inflate the layout for this fragment


        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                selectRoom(viewModelRoom.getRoom())
                binding.btnAddRoomUser.setOnClickListener {
                    view?.findNavController()?.navigate(R.id.action_roomFragment2_to_addRoomFragment2)
                }
            } else
                binding.btnAddRoomUser.visibility = View.GONE
        }

        return view
    }

    fun selectRoom(list: MutableList<Rooms>) {
        adapter = AdapterRoomUser(list, object : rvInter {
            override fun onClickRoom(position: Int) {
//                Toast.makeText(activity,"bạn đã click vào ${adapter.currentList[position].name}",
//                    Toast.LENGTH_SHORT).show()

                val bundle = Bundle()
                bundle.putString("id", adapter.currentList[position]._id)
                bundle.putString("bed", adapter.currentList[position].beds)
                bundle.putString("decs", adapter.currentList[position].description)
                bundle.putString("loc", adapter.currentList[position].location)
                bundle.putString("name", adapter.currentList[position].name)
                bundle.putString("price", adapter.currentList[position].prices.toString())
                bundle.putString("status", adapter.currentList[position].status)
                viewModel.checkAdmin { isAdmin ->
                    if (isAdmin) {
                        val navController = view?.findNavController()
                        navController?.navigate(
                            R.id.action_roomFragment2_to_updateRoomFragment2,
                            bundle
                        )
                    } else
                        Toast.makeText(
                            activity, "bạn đã click vào ${adapter.currentList[position].name}",
                            Toast.LENGTH_SHORT
                        ).show()
                }


            }

            override fun onItemLongClick(position: Int) {
                // Lưu trữ vị trí của item được long click
                longClickedPosition = position

                // Hiển thị context menu với vị trí position của item trong RecyclerView

                registerForContextMenu(rvRoom)
                rvRoom.showContextMenuForChild(rvRoom.getChildAt(position))
                unregisterForContextMenu(rvRoom)
            }
        }, this)


        rvRoom.adapter = adapter
        rvRoom.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.HORIZONTAL,
            false
        )
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelRoom.rooms.observe(viewLifecycleOwner, { rooms ->
            adapter.setData(rooms.toMutableList())
        })

    }


    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        // Lấy đối tượng MenuInflater từ Fragment
        val inflater = requireActivity().menuInflater
        val position = longClickedPosition
        menu?.setHeaderTitle("Room ${adapter.currentList[position].name}")
        // Sử dụng MenuInflater để inflate tập tin menu resource
        inflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                // Lấy vị trí của item được long click
//                val position = longClickedPosition
//                if (position != -1) {
//                    viewModelRoom.deleteRoom(adapter.currentList[position]._id)
//                    longClickedPosition = -1 // Reset giá trị của biến tạm
//                    var ad = AlertDialog.Builder(requireContext())
//                    ad.setTitle("Delete record")
//                    ad.setMessage("Delete success")
//                    ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//                        Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show()
//                    }).show()
//                    return true
//                }


            }
            R.id.member -> {
                val position = longClickedPosition
                if (position != -1) {
                    val bundle = Bundle()
                    bundle.putString("id", adapter.currentList[position]._id)
                    bundle.putString("name", adapter.currentList[position].name)
                    val navController = view?.findNavController()
                    navController?.navigate(
                        R.id.action_roomFragment2_to_registerRMFragment,
                        bundle
                    )
                    longClickedPosition = -1
                }
            }
        }
        return super.onContextItemSelected(item)
    }


}