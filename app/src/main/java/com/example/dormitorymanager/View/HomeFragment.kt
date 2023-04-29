package com.example.dormitorymanager.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.Chart.ChartActivity
import com.example.dormitorymanager.View.RoomManagerAdmin.RoomActivity
import com.example.dormitorymanager.View.StudentRegisterRoom.RoomRegisterActivity
import com.example.dormitorymanager.ViewModel.ViewModelUser


class HomeFragment : Fragment() {
    private lateinit var viewModel : ViewModelUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view :View = inflater.inflate(R.layout.fragment_home,container,false)
        val btnstart: Button = view.findViewById(R.id.btnstart)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        btnstart.setOnClickListener {
            Log.e("home", viewModel.getCurrentUser())
            if (viewModel.checkLogin()) {
                viewModel.checkAdmin { isAdmin ->
                    if (isAdmin) {
//                        val inten = Intent(context, RoomActivity::class.java)
                        val inten = Intent(context, ChartActivity::class.java)
                        startActivity(inten)
                    } else {
                        val intentuser = Intent(context, RoomRegisterActivity::class.java)
                        startActivity(intentuser)

                    }
                }
            }
            else{
//                var intent = Intent(activity, loginActivity::class.java)
                val intent = activity?.let { Intent(it, LoginActivity::class.java) }
                startActivity(intent)
            }
        }
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,requireView().findNavController())
                ||super.onOptionsItemSelected(item)
    }

}