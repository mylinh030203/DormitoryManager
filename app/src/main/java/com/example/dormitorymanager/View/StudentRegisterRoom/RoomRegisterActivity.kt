package com.example.dormitorymanager.View.StudentRegisterRoom

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.dormitorymanager.MainActivity
import com.example.dormitorymanager.R
import com.example.dormitorymanager.View.Chart.ChartActivity
import com.example.dormitorymanager.View.HomeFragment
import com.example.dormitorymanager.View.InformationUser.InformationUserActivity
import com.example.dormitorymanager.View.LoginActivity
import com.example.dormitorymanager.View.StudentManagerAdmin.StudentActivity
import com.example.dormitorymanager.View.StudentManagerAdmin.UpdateStudentFragment
import com.example.dormitorymanager.ViewModel.ViewModelStudent
import com.example.dormitorymanager.ViewModel.ViewModelUser
import com.example.dormitorymanager.databinding.ActivityRoomBinding
import com.example.dormitorymanager.databinding.ActivityRoomRegisterBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class RoomRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomRegisterBinding
    private lateinit var viewModelStudent: ViewModelStudent
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: ViewModelUser
    private lateinit var imageView: CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomRegisterBinding.inflate(layoutInflater)
        viewModelStudent = ViewModelProvider(this).get(ViewModelStudent::class.java)
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
                                avatar ->
                            Picasso.get().load(avatar).into(imageView)
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
                R.id.homeU -> {
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.infoU->{
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
                                        bundle.putString("id", student?._id)
                                        bundle.putString("fullname", student?.fullname)
                                        bundle.putString("phone", student?.phone)
                                        bundle.putString("gender", student?.gender)
                                        bundle.putString("idStudent", student?.idStudent)
                                        bundle.putString("classStd", student?.classStd)
                                        bundle.putString("avatar", student?.avatar)
//                                        navController.navigate(R.id.action_homeFragment_to_updateStudentFragment2, bundle)
                                        val inten = Intent(this, InformationUserActivity::class.java)
                                        startActivity(inten, bundle)

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
                R.id.logoutU -> {
                    Log.e("dc",viewModel.checkLogin().toString())
                    viewModel.Logout()
                    Log.e("dd",viewModel.checkLogin().toString())
                    finish()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }
                R.id.chartU->{
                    val intent = Intent(this, ChartActivity::class.java)
                    startActivity(intent)
                }

                R.id.exitU -> Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show()
            }
            true
        }
        if (viewModel.checkLogin() == false) {
            header.findViewById<TextView>(R.id.textView).setText("")
        }

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


}