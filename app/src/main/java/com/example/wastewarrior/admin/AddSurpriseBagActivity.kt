package com.example.wastewarrior.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wastewarrior.databinding.ActivityAddSurpriseBagBinding
import com.example.wastewarrior.models.SurpriseBag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddSurpriseBagActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityAddSurpriseBagBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSurpriseBagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addSurpriseBagButton.setOnClickListener {
            // Get user inputs from EditText views
            val name = binding.editSurpriseBagName.text.toString()
            val quantity = binding.editSurpriseBagQuantity.text.toString().toInt()
            val price = binding.editSurpriseBagPrice.text.toString().toDouble()

            // Create a new SurpriseBag object with user inputs
            val newSurpriseBag = SurpriseBag(
                name = name,
                quantity = quantity,
                isFavourite = false,
                price = price,
                category = "xgjx"
            )

            // Perform actions to add the new surprise bag to Firestore
            addSurpriseBagToFirestore(newSurpriseBag)
        }
    }

    private fun addSurpriseBagToFirestore(surpriseBag: SurpriseBag) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("restaurants").document(it)

            // Retrieve the existing surprises list and update it
            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("surprises") as MutableList<HashMap<String, Any>>?
                    surprises?.add(surpriseBag.toHashMap())
                    surprises?.let {
                        restaurantRef.update("surprises", it)
                            .addOnSuccessListener {
                                showToast("Surprise bag added successfully")
                                val intent = Intent(this, SurpriseBagsActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                showToast("Failed to add surprise bag")
                            }
                    }
                } else {
                    showToast("Restaurant document not found")
                }
            }.addOnFailureListener { exception ->
                showToast("Error: ${exception.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
