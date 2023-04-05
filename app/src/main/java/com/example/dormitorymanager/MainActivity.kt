package com.example.dormitorymanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding:  ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel : ViewModelUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        setContentView(binding.root)
        setupDrawerLayout()
        menu()
    }

    private fun setupDrawerLayout() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawerLayout = binding.drawLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun menu(){

        val myNavHostFragment   = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
//        val navController  = this.findNavController(R.id.myNavHostFragment)
        val navController = myNavHostFragment.navController
        val navView: NavigationView = binding.navLeftmenu
        NavigationUI.setupWithNavController(navView, navController)
        drawerLayout = binding.drawLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout )
        val header: View =  binding.navLeftmenu.getHeaderView(0)
        val textView = header.findViewById<TextView>(R.id.textView)
        val textView2 = header.findViewById<TextView>(R.id.textView2)

        if(viewModel.checkLogin()){
            val uid = viewModel.user?.uid.toString()
            val users = viewModel.dbRef.child(uid)
            users.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val name:String = snapshot.child("name").value.toString()
                        var role_id : String = snapshot.child("role_id").value.toString()
                        textView.text = name
                        textView2.text = checkRole(role_id)
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
                R.id.home -> {
                    navController.navigate(R.id.action_homeFragment_to_roomFragment)
                }
                R.id.logout -> {
                    viewModel.Logout()
                    finish()
                }
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


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }

}