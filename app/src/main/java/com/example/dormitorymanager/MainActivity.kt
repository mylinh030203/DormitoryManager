package com.example.dormitorymanager

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.dormitorymanager.View.Chart.ChartActivity
import com.example.dormitorymanager.View.InformationUser.InformationUserActivity
import com.example.dormitorymanager.View.LoginActivity
import com.example.dormitorymanager.View.RoomManagerAdmin.RoomActivity
import com.example.dormitorymanager.View.StudentManagerAdmin.StudentActivity
import com.example.dormitorymanager.View.StudentRegisterRoom.RoomRegisterActivity
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: ViewModelUser
    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var imageView: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
        setContentView(binding.root)


        setupDrawerLayout()
        menu()

    }

    private fun setupDrawerLayout() {
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.ic_dm_background)))
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

    fun menu() {

        val myNavHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = myNavHostFragment.navController

        val navView: NavigationView = binding.navLeftmenu
        NavigationUI.setupWithNavController(navView, navController)


        drawerLayout = binding.drawLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        val header: View = binding.navLeftmenu.getHeaderView(0)
        val textView = header.findViewById<TextView>(R.id.textView)
        val textView2 = header.findViewById<TextView>(R.id.textView2)
        imageView = header.findViewById(R.id.profile_image)

        if (viewModel.checkLogin()) {
            val uid = viewModel.user.value?.uid
            val users = viewModel.dbRef.child(uid.toString())
            //lấy thông tin đăng nhập
            users.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name: String = snapshot.child("name").value.toString()
                        var role_id: String = snapshot.child("role_id").value.toString()
                        viewModelStudent.getAvatar(uid.toString(), {
                            avatar ->Picasso.get().load(avatar).into(imageView)
                        })

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
                    if (viewModel.checkLogin()) {
                        viewModel.checkAdmin { isAdmin ->
                            if (isAdmin) {
                                val inten = Intent(this, RoomActivity::class.java)
                                startActivity(inten)
                            } else {
                                val intentuser = Intent(this, RoomRegisterActivity::class.java)
                                startActivity(intentuser)

                            }
                        }

                    }
                }
                R.id.info -> {
                    if (viewModel.checkLogin()) {
                        viewModel.checkAdmin { isAdmin ->
                            if (isAdmin) {
                                val inten = Intent(this, StudentActivity::class.java)
                                startActivity(inten)
                            } else {
                                var id = viewModel.getCurrentUser()
                                viewModelStudent.getStudent()
                                viewModelStudent.students.observe(this, { students ->
                                    // Xử lý dữ liệu sinh viên đã được cập nhật
                                    // Students là danh sách sinh viên
                                    val studentId = id // Thay đổi giá trị studentId tùy vào nhu cầu
                                    val student = viewModelStudent.getStudentById(studentId)
                                    if (student != null) {

                                        val bundle = Bundle()
                                        bundle.putString("id", student._id)
                                        bundle.putString("fullname", student.fullname)
                                        bundle.putString("phone", student.phone)
                                        bundle.putString("gender", student.gender)
                                        bundle.putString("idStudent", student.idStudent)
                                        bundle.putString("classStd", student.classStd)
                                        bundle.putString("avatar", student.avatar)
                                        val inten = Intent(this, InformationUserActivity::class.java)
//                                        inten.putExtras(bundle)
                                        startActivity(inten)

                                    } else {
                                        // Xử lý khi không tìm thấy sinh viên với studentId tương ứng
                                    }
                                })
                                // Bắt đầu giao dịch FragmentTransaction


                            }
                        }

                    } else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.chart->{
                    val intent = Intent(this, ChartActivity::class.java)
                    startActivity(intent)
                }
                R.id.logout -> {
                    Log.e("dc", viewModel.checkLogin().toString())
                    viewModel.Logout()
                    Log.e("dd", viewModel.checkLogin().toString())
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }

                R.id.exit -> Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show()
            }
            true
        }
        if (viewModel.checkLogin() == false) {
            header.findViewById<TextView>(R.id.textView).setText("")
        }

//        val bottomNavigationView = binding.bottomNavigationView
//        bottomNavigationView.setOnItemReselectedListener {
//            when(it.itemId){
//                R.id.home->{
//                    Log.e("aaa",viewModel.checkLogin().toString())
//                    if(viewModel.checkLogin()){
//                        replaceFragment(HomeFragment())
//                    }
//                    else{
//                        var intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//
//                }
//                R.id.room->{
//                    Log.e("aaa",viewModel.checkLogin().toString())
//                    if(viewModel.checkLogin()) {
//                        replaceFragment(RoomFragment())
//                    }else{
//                        var intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//
//                else -> {
//
//                }
//            }
//            true
//        }

    }

    fun checkRole(RoleID: String): String {
        if (RoleID.equals("2")) {
            return "Student"
        } else if (RoleID.equals("1")) {
            return "Admin"
        }
        return ""
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }


}