package com.example.wastewarrior.admin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.wastewarrior.R
import com.example.wastewarrior.RegisterActivity
import com.example.wastewarrior.databinding.ActivityRegisterUserBinding
import com.example.wastewarrior.databinding.ActivityRestaurantInfoBinding
import com.example.wastewarrior.models.SurpriseBag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RestaurantInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRestaurantInfoBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("MapPrefs", MODE_PRIVATE)

        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val name = getSavedNameFromSharedPreferences()
        binding.restaurantName.setText(name)
        binding.tvLocation.setOnClickListener {
            val name = binding.restaurantName.text.toString()
            if (name.isNotEmpty()){
                saveNameToSharedPreferences(name)
                startActivity(Intent(this, MapActivity::class.java))
            }else{
                Toast.makeText(this,"Please enter restaurant name",Toast.LENGTH_LONG).show()
            }
        }

        binding.save.setOnClickListener{
            var name=getSavedNameFromSharedPreferences()
            if (name.isNullOrBlank()){
                name= binding.restaurantName.text.toString()
            }
            var location:LatLng?=null
            location=getSavedLocationFromSharedPreferences()
            Log.i("app",location.toString())
            if(location==null){
                Toast.makeText(this,"Please select location",Toast.LENGTH_LONG).show()
            }else{
            if(name.isNotEmpty()){
                location?.let {
                    userId?.let {
                        val profileRef = db.collection("restaurants").document(it)

                        val restaurantData = hashMapOf(
                            "name" to name,
                            "location" to location,
                            "surprises" to emptyList<SurpriseBag>()
                        )

                        profileRef.set(restaurantData)
                            .addOnSuccessListener {
                                Log.d("res", "Restaurant profile added")
                                startActivity(Intent(this, SurpriseBagsActivity::class.java))
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "res",
                                    "Error adding restaurant profile",
                                    e
                                )
                                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }
            }

        }

    }


    private fun saveNameToSharedPreferences(name: String) {
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.apply()
    }

    private fun getSavedNameFromSharedPreferences(): String? {
        val name = sharedPreferences.getString("name","")

        return name
    }
    private fun getSavedLocationFromSharedPreferences(): LatLng? {
        val latitude = sharedPreferences.getFloat("latitude", 0f)
        val longitude = sharedPreferences.getFloat("longitude", 0f)

        if (latitude != 0f && longitude != 0f) {
            return LatLng(latitude.toDouble(), longitude.toDouble())
        }
        return null
    }


}