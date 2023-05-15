package com.example.dormitorymanager.View.RoomManagerAdmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom

class AddRoomFragment : Fragment() {
    private lateinit var viewModelRoom: ViewModelRoom

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        var view: View = inflater.inflate(R.layout.fragment_add_room,container,false)
        var edtbed = view.findViewById<EditText>(R.id.beds)
        var edtDesc = view.findViewById<EditText>(R.id.desc)
        var edtLoc = view.findViewById<EditText>(R.id.loc)
        var edtName = view.findViewById<EditText>(R.id.nameR)
        var edtprice = view.findViewById<EditText>(R.id.price)
        var edtStatus = view.findViewById<EditText>(R.id.status)
        var btnadd :Button = view.findViewById(R.id.btnadd)
        btnadd.setOnClickListener {
            viewModelRoom.addRoom(edtbed.text.toString(),edtDesc.text.toString(),
                                    edtLoc.text.toString(),edtName.text.toString(),
                                    edtprice.text.toString().toLong(),edtStatus.text.toString())
            viewModelRoom.collectionRoom.get().addOnSuccessListener {

                    Toast.makeText(activity, "Add room success", Toast.LENGTH_SHORT).show()
                    edtbed.setText("")
                    edtDesc.setText("")
                    edtLoc.setText("")
                    edtName.setText("")
                    edtprice.setText("")
                    edtStatus.setText("")

                view.findNavController().navigate(R.id.action_addRoomFragment2_to_roomFragment2)

            }.addOnFailureListener { error ->
                Toast.makeText(activity, "Add room failer", Toast.LENGTH_SHORT).show()
            }

        }
        return view
    }



}