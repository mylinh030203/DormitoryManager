package com.example.dormitorymanager.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelUser
import kotlinx.android.synthetic.main.fragment_room.view.*


class RoomFragment : Fragment() {
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewModel : ViewModelUser
    private lateinit var adapter: AdapterRoom
    private lateinit var rvRoom :RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_room,container,false)
        var btnaddroom = view.findViewById<Button>(R.id.btnAddRoom)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        rvRoom = view.findViewById(R.id.rvRoom)
        // Inflate the layout for this fragment
        selectRoom(viewModelRoom.getRoom())

        btnaddroom.setOnClickListener {
            view.findNavController().navigate(R.id.action_roomFragment2_to_addRoomFragment2)
        }
        return view
    }

    fun selectRoom(list: MutableList<Rooms>){
        adapter = AdapterRoom(list,object : rvInter{
            override fun onClickRoom(position: Int){
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
                navController?.navigate(R.id.action_roomFragment2_to_updateRoomFragment2, bundle)
//                fragmentManager?.beginTransaction()
//                    ?.replace(R.id.drawLayout, updateRoomFragment)
//                    ?.addToBackStack(null)
//                    ?.commit()

            }
        },this)
        rvRoom.adapter = adapter
        rvRoom.layoutManager = GridLayoutManager(context,
            4,
            GridLayoutManager.HORIZONTAL,
            false)
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelRoom.rooms.observe(viewLifecycleOwner, { rooms ->
            adapter.setData(rooms.toMutableList())
        })


    }





}