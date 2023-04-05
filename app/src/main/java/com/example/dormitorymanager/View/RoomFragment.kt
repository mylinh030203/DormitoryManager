package com.example.dormitorymanager.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelUser


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
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        rvRoom = view.findViewById(R.id.rvRoom)
        // Inflate the layout for this fragment
        selectRoom(viewModelRoom.getRoom())
        return view
    }

    fun selectRoom(list: MutableList<Rooms>){
        adapter = AdapterRoom(list,object : rvInter{
            override fun onClickRoom(position: Int){
                Toast.makeText(activity,"bạn đã click vào ${adapter.currentList[position].name}",
                    Toast.LENGTH_SHORT).show()
            }
        },this)
        rvRoom.adapter = adapter
        rvRoom.layoutManager = GridLayoutManager(context,
            3,
            GridLayoutManager.HORIZONTAL,
            false)
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelRoom.rooms.observe(viewLifecycleOwner, { rooms ->
            adapter.setData(rooms.toMutableList())
        })
    }

}