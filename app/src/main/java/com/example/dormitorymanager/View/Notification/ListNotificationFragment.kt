package com.example.dormitorymanager.View.Notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Notification
import com.example.dormitorymanager.Model.StudentInfor
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.StudentManagerAdmin.AdapterStudent
import com.example.dormitorymanager.View.rvInter
import com.example.dormitorymanager.ViewModel.NotificationViewModel
import com.example.dormitorymanager.ViewModel.ViewModelStudent


class ListNotificationFragment : Fragment() {
    private lateinit var adapter: AdapterNotification
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModelNotification: NotificationViewModel
    private var longClickedPosition: Int = -1// Danh sách thông báo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_notification, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        viewModelNotification = ViewModelProvider(this).get(NotificationViewModel::class.java)
        selectNotification(viewModelNotification.selectNotification())
        return view
    }

    fun selectNotification(list: MutableList<Notification>) {
        adapter = AdapterNotification(list, object : rvInter {
            override fun onClickNotification(position: Int) {
                val bundle = Bundle()
                bundle.putString("id", adapter.currentList[position].id)
                bundle.putString("message", adapter.currentList[position].message)
                bundle.putString("title", adapter.currentList[position].title)
                bundle.putString("id_user", adapter.currentList[position].id_user.get(0))
                bundle.putString("time", adapter.currentList[position].timestamp.toString())
            }

            override fun onItemLongClick(position: Int) {
                // Lưu trữ vị trí của item được long click
                longClickedPosition = position

                // Hiển thị context menu với vị trí position của item trong RecyclerView

                registerForContextMenu(recyclerView)
                recyclerView.showContextMenuForChild(recyclerView.getChildAt(position))
                unregisterForContextMenu(recyclerView)
            }
        }, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(
            context,
            4,
            GridLayoutManager.HORIZONTAL,
            false
        )
        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.

        viewModelNotification.notification.observe(viewLifecycleOwner, { notification ->
                adapter.setData(notification.toMutableList())
        })

    }

}