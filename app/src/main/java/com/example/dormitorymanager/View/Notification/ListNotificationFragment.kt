package com.example.dormitorymanager.View.Notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormitorymanager.Model.Notification
import com.example.dormitorymanager.R


class ListNotificationFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: AdapterNotification
    private lateinit var notifications: List<Notification> // Danh sách thông báo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_notification, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
//
//        // Khởi tạo danh sách thông báo (notifications)
//        // ...
//
//        notificationAdapter = AdapterNotification(notifications)
//        recyclerView.adapter = notificationAdapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

}