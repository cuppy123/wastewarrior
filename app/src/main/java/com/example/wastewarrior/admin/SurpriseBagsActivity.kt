package com.example.wastewarrior.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wastewarrior.R
import com.example.wastewarrior.databinding.ActivitySurpriseBagsBinding
import com.example.wastewarrior.models.SurpriseBag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SurpriseBagsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivitySurpriseBagsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurpriseBagsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.surpriseBagsRecyclerView.layoutManager = layoutManager

        // Retrieve surprise bags from Firestore
        retrieveSurpriseBags()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, AddSurpriseBagActivity::class.java)
            startActivity(intent)
        }

    }

    private fun retrieveSurpriseBags() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.i("bags",userId.toString())
        userId?.let {
                val restaurantRef = db.collection("restaurants").document(it)

                restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val surprises = documentSnapshot.get("surprises") as List<HashMap<String, Any>>?
                        if (surprises != null) {
                            val surpriseBags = surprises.map {
                                val name = it["name"] as String
                                val quantity = (it["quantity"] as Long).toInt()
                                val isFavourite = it["isFavourite"] as Boolean
                                val price = it["price"] as Double
                                SurpriseBag(name, quantity, isFavourite, price)
                            }

                            val surpriseBagsAdapter = SurpriseBagAdapter(surpriseBags)
                            binding.surpriseBagsRecyclerView.adapter = surpriseBagsAdapter
                        } else {
                            // No surprise bags found
                            Log.i("bags","Nobags")
                        }
                    } else {
                        // Restaurant document doesn't exist
                        Log.i("bags","Nodoc")
                    }
                }.addOnFailureListener { exception ->
                    // Handle failure to retrieve surprise bags
                    Log.i("bags",exception.message.toString())
                }
            }
    }
}
