package com.example.wastewarrior

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.wastewarrior.admin.LoginActivity
import com.example.wastewarrior.databinding.ActivityLandingPageBinding

class LandingPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)

        binding = ActivityLandingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.adminlogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.userlogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }




    }


}