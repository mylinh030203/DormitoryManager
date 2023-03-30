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
import androidx.lifecycle.ViewModelProvider
import com.example.dormitorymanager.View.RegisterActivity
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityMainBinding
import com.example.dormitorymanager.databinding.ActivityRegisterBinding
import com.google.android.material.navigation.NavigationView
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
        //đổi màu cho các icon trong navleft
        binding.navLeftmenu.itemIconTintList = null
        //lắng nghe sự kiện click lên các sự kiện menu
        binding.navLeftmenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
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

        binding.btnstart.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

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