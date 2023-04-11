package com.example.dormitorymanager.View

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class updateRoomFragment : Fragment() {
    private lateinit var viewModelRoom: ViewModelRoom
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        val view: View = inflater.inflate(R.layout.fragment_update_room, container, false)
        var beds = view.findViewById<EditText>(R.id.bedUpdate)
        var edtDesc = view.findViewById<EditText>(R.id.descUpdate)
        var edtLoc = view.findViewById<EditText>(R.id.locUpdate)
        var edtName = view.findViewById<EditText>(R.id.nameRUpdate)
        var edtprice = view.findViewById<EditText>(R.id.priceUpdate)
        var edtStatus = view.findViewById<EditText>(R.id.statusUpdate)
        var btnUpdate: Button = view.findViewById(R.id.btnupdate)
        var btnDelete:Button = view.findViewById(R.id.btndelete)
        var id = arguments?.getString("id", "8")
        beds.setText(arguments?.getString("bed", "8"))
        edtDesc.setText(arguments?.getString("decs", "8"))
        edtLoc.setText(arguments?.getString("loc", "8"))
        edtName.setText(arguments?.getString("name", "8"))
        edtprice.setText(arguments?.getString("price", "8"))
        edtStatus.setText(arguments?.getString("status", "8"))
        btnUpdate.setOnClickListener {
            viewModelRoom.updateRoom(
                id.toString(),
                beds.text.toString(),
                edtDesc.text.toString(),
                edtLoc.text.toString(),
                edtName.text.toString(),
                edtprice.text.toString().toLong(),
                edtStatus.text.toString()
            )
        }

        viewModelRoom.updateResult.observe(viewLifecycleOwner, Observer { updateSuccess ->
            if (updateSuccess == true) {
                // Hiển thị Toast khi cập nhật thành công
                Toast.makeText(context, "Update success", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.action_updateRoomFragment2_to_roomFragment2)
            } else {
                Toast.makeText(context, "Update failure", Toast.LENGTH_SHORT).show()
            }
        })
        btnDelete.setOnClickListener {
            viewModelRoom.deleteRoom(id.toString())
            var ad = AlertDialog.Builder(requireContext())
            ad.setTitle("Delete record")
            ad.setMessage("Delete success")
            ad.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show()
            }).show()
            view.findNavController().navigate(R.id.action_updateRoomFragment2_to_roomFragment2)
        }



        return view
    }

}