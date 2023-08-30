package com.example.wastewarrior

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.wastewarrior.admin.RestaurantInfoActivity
import com.example.wastewarrior.user.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.wastewarrior.databinding.ActivityRegisterUserBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val roleSpinner = binding.roleSpinner
        val roleOptions = arrayOf("User", "Restaurant")

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roleOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK) // Change text color here
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.WHITE) // Change text color here
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter
        binding.register.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.registerConfirmPassword.text.toString()
            val selectedRole = binding.roleSpinner.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                registerUser(email, password,selectedRole)
            } else {
                // Handle empty fields or password mismatch
                if (password!=confirmPassword){
                    Toast.makeText(this,"Passwords do not match",Toast.LENGTH_LONG).show()
                } else{
                    Toast.makeText(this,"Please fill all fields",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun registerUser(email: String, password: String, selectedRole: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User registration successful")
                    if(selectedRole=="User"){
                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        startActivity(Intent(this, RestaurantInfoActivity::class.java))
                    }
                } else {
                    Log.w(TAG, "User registration failed", task.exception)
                    Toast.makeText(this,task.exception?.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }

}