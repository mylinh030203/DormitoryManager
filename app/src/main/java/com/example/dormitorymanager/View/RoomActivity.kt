package com.example.dormitorymanager.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.Model.Rooms
import com.example.dormitorymanager.R
import com.example.dormitorymanager.ViewModel.ViewModelRoom
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityRoomBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.FieldPosition

class RoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomBinding
    private lateinit var viewModelRoom: ViewModelRoom
    private lateinit var viewModel : ViewModelUser
    private lateinit var adapter: AdapterRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelRoom = ViewModelProvider(this).get(ViewModelRoom::class.java)
        menu()
        selectRoom(viewModelRoom.getRoom())
        setContentView(binding.root)
    }

    fun addRoom(){

    }


    fun selectRoom(list: MutableList<Rooms>){
         adapter = AdapterRoom(list,object : rvInter{
            override fun onClickRoom(position: Int){
                Toast.makeText(this@RoomActivity,"bạn đã click vào ${adapter.currentList[position].name}",Toast.LENGTH_SHORT).show()
            }
        },this@RoomActivity)
        binding.rvRoom.adapter = adapter
        binding.rvRoom.layoutManager =GridLayoutManager(this,
            3,
            GridLayoutManager.HORIZONTAL,
            false)

        //Hàm viewModelRoom.rooms.observe() được sử dụng để đăng ký một Observer cho LiveData được trả về từ ViewModel. Khi dữ liệu trong LiveData được cập nhật, Observer sẽ nhận được thông báo và có thể thực hiện một số hành động liên quan đến dữ liệu đó.
        viewModelRoom.rooms.observe(this, { rooms ->
            adapter.setData(rooms.toMutableList())
        })
    }

//menu
    fun menu(){
        //dùng nút bấm để hiển thị navigation
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        val header: View =  binding.navLeftmenu.getHeaderView(0)
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
                R.id.home -> {var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)}
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
//end menu
}