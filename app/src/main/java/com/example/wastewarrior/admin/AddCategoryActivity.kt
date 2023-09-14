package com.example.wastewarrior.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wastewarrior.R
import com.example.wastewarrior.databinding.ActivityAddCategoryBinding
import com.example.wastewarrior.databinding.ActivityAddSurpriseBagBinding
import com.example.wastewarrior.models.Category
import com.example.wastewarrior.models.SurpriseBag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddCategoryActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityAddCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addCategoryButton.setOnClickListener {
            // Get user inputs from EditText views
            val name = binding.categoryName.text.toString()

            // Create a new SurpriseBag object with user inputs
            val newCategory = Category(
                name = name,
            )
            print(name)

            // Perform actions to add the new surprise bag to Firestore
            addSurpriseBagToFirestore(newCategory)
        }
    }

    private fun addSurpriseBagToFirestore(surpriseBag: Category) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("restaurants").document(it)

            // Retrieve the existing surprises list and update it
            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("categories") as MutableList<HashMap<String, Any>>?
                    surprises?.add(surpriseBag.toHashMap())
                    surprises?.let {
                        restaurantRef.update("categories", it)
                            .addOnSuccessListener {
                                showToast("Category added successfully")
                                val intent = Intent(this, SurpriseBagsActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                showToast("Failed to add category bag")
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