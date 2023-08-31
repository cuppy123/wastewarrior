package com.example.wastewarrior.admin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wastewarrior.R
import com.example.wastewarrior.RegisterActivity
import com.example.wastewarrior.databinding.ActivityLoginBinding
import com.example.wastewarrior.user.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MapPrefs", MODE_PRIVATE)
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val uid = null
        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //checkrole
                        val uid = auth.currentUser?.uid

                        if (uid != null) {
                            val role =getSavedRoleFromSharedPreferences()

                            if (role.equals("restaurant")) {
                                val intent = Intent(this, SurpriseBagsActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }


                    } else {
                        // Login failed, handle error
                        Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }



    }
    private fun getSavedRoleFromSharedPreferences(): String? {
        val role = sharedPreferences.getString("role","")

        return role
    }
}