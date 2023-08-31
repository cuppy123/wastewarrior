package com.example.wastewarrior

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.wastewarrior.admin.LoginActivity
import com.example.wastewarrior.databinding.ActivityLandingPageBinding

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)

        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("MapPrefs", MODE_PRIVATE)

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.adminlogin.setOnClickListener {
            saveRoleToSharedPreferences("restaurant")
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.userlogin.setOnClickListener {
            saveRoleToSharedPreferences("user")
            startActivity(Intent(this, LoginActivity::class.java))
        }




    }
    private fun saveRoleToSharedPreferences(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString("role", role)
        editor.apply()
    }



}