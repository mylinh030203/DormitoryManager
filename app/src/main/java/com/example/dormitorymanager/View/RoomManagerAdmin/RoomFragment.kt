package com.example.dormitorymanager.View.RoomManagerAdmin

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


class RoomFragment : Fragment() {
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewModel: ViewModelUser
    private lateinit var adapter: AdapterRoom
    private lateinit var rvRoom: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_room, container, false)
        var btnaddroom = view.findViewById<Button>(R.id.btnAddRoom)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        rvRoom = view.findViewById(R.id.rvRoom)
        // Inflate the layout for this fragment


        viewModel.checkAdmin { isAdmin ->
            if (isAdmin) {
                selectRoom(viewModelRoom.getRoom())
                registerForContextMenu(rvRoom)
                btnaddroom.setOnClickListener {
                    view.findNavController().navigate(R.id.action_roomFragment2_to_addRoomFragment2)
                }
            } else
                btnaddroom.visibility = View.GONE
        }

        return view
    }

    fun selectRoom(list: MutableList<Rooms>) {
        adapter = AdapterRoom(list, object : rvInter {
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

//                fragmentManager?.beginTransaction()
//                    ?.replace(R.id.drawLayout, updateRoomFragment)
//                    ?.addToBackStack(null)
//                    ?.commit()

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
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(100, 11, 1, "Delete Room")
        menu?.add(100, 12, 2, "Member of room")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            11 -> {
                viewModelRoom.deleteRoom(arguments?.getString("id").toString())
                var ad = AlertDialog.Builder(requireContext())
                ad.setTitle("Delete record")
                ad.setMessage("Delete success")
                ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show()
                }).show()
            }
            12 -> {

            }
        }
        return super.onContextItemSelected(item)
    }


}