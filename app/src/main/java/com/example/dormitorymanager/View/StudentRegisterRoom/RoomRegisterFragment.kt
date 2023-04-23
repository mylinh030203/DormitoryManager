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
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewModel: ViewModelUser
    private lateinit var adapter: AdapterRoomUser
    private lateinit var rvRoom: RecyclerView
    private var longClickedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_room_register, container, false)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        rvRoom = view.findViewById(R.id.rvRoomUser)
        var btnAddRoomUser = view.findViewById<Button>(R.id.btnAddRoom)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        // Inflate the layout for this fragment
        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                selectRoom(viewModelRoom.getRoom())
                btnAddRoomUser.setOnClickListener {
                    view?.findNavController()
                        ?.navigate(R.id.action_roomFragment2_to_addRoomFragment2)
                }
            } else
                selectRoom(viewModelRoom.getRoom())
            btnAddRoomUser.visibility = View.GONE
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

                val navController = view?.findNavController()
                navController?.navigate(
                    R.id.action_roomRegisterFragment_to_infoRoomFragment,
                    bundle
                )

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
            R.id.member -> {
                val position = longClickedPosition
                if (position != -1) {
                    val bundle = Bundle()
                    bundle.putString("id", adapter.currentList[position]._id)
                    bundle.putString("name", adapter.currentList[position].name)
                    val navController = view?.findNavController()
//                    navController?.navigate(
//                        R.id.action_roomRegisterFragment_to_registerRMFragment3,
//                        bundle
//                    )
                    longClickedPosition = -1
                }
            }
        }
        return super.onContextItemSelected(item)
    }


}