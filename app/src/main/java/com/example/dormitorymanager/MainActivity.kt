package com.example.dormitorymanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.View.LoginActivity
import com.example.dormitorymanager.View.RegisterActivity
import com.example.dormitorymanager.View.RoomActivity
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityMainBinding
import com.example.dormitorymanager.databinding.ActivityRegisterBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:  ActivityMainBinding
    private lateinit var viewModel : ViewModelUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)

//menu
        menu()
//endMenu
        binding.btnstart.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun menu(){
        //dùng nút bấm để hiển thị navigation
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        val header:View =  binding.navLeftmenu.getHeaderView(0)
        header.findViewById<TextView>(R.id.textView).text = intent.getStringExtra("fullname")
        header.findViewById<TextView>(R.id.textView2).text = checkRole(intent.getStringExtra("role").toString())


        if(viewModel.checkLogin()){
            val uid = viewModel.user?.uid.toString()
            val users = viewModel.dbRef.child(uid)
            users.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val name:String = snapshot.child("name").value.toString()
                        var role_id : String = snapshot.child("role_id").value.toString()
                        header.findViewById<TextView>(R.id.textView).text = name
                        header.findViewById<TextView>(R.id.textView2).text = checkRole(role_id)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        //đổi màu cho các icon trong navleft
        binding.navLeftmenu.itemIconTintList = null
        //lắng nghe sự kiện click lên các sự kiện menu
        binding.navLeftmenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {var intent = Intent(this, RoomActivity::class.java)
                    startActivity(intent)
                    finish()}
                R.id.logout -> {
                    viewModel.Logout()
                    finish()}
                R.id.exit -> Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show()
            }
            true
        }
        if(viewModel.user==null){
            header.findViewById<TextView>(R.id.textView).setText("")
        }
    }


    fun checkRole(RoleID:String):String{
        if(RoleID.equals("2")){
            return "Sinh Viên"
        }else if(RoleID.equals("1")){
            return "Quản lý"
        }
        return ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}