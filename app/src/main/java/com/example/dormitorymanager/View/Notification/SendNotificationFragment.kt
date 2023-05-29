package com.example.dormitorymanager.View.Notification

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.NotificationViewModel
import com.google.firebase.Timestamp

class SendNotificationFragment : Fragment() {

    private lateinit var viewModelNotification: NotificationViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelNotification = ViewModelProvider(this).get(NotificationViewModel::class.java)
        var view = inflater.inflate(R.layout.fragment_send_notification, container, false)
        var etTitle = view.findViewById<EditText>(R.id.etTitle)
        var etMessage = view.findViewById<EditText>(R.id.etMessage)
        var idNotification = view.findViewById<EditText>(R.id.idNotification)
        var btnSend = view.findViewById<Button>(R.id.btnSendNotification)
        var btnList = view.findViewById<Button>(R.id.btnList)
        var idroom = arguments?.getString("id","10")

        btnSend.setOnClickListener {
            var title = etTitle.text.toString()
            var message = etMessage.text.toString()
            var id = idNotification.text.toString()
            var time = Timestamp.now()
            viewModelNotification.getStudentInRoom(idroom.toString()){idUser->
                viewModelNotification.sendNotificationToStudents(id, idUser, title, message,time)
            }
            val navController = view?.findNavController()
            navController?.navigate(
                R.id.action_sendNotificationFragment2_to_listNotificationFragment2
            )
        }
        btnList.setOnClickListener {
            val navController = view?.findNavController()
            navController?.navigate(
                R.id.action_sendNotificationFragment2_to_listNotificationFragment2
            )
        }
        return view
    }

}