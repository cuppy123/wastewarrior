package com.example.wastewarrior.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wastewarrior.R
import com.example.wastewarrior.databinding.ActivitySurpriseBagsBinding
import com.example.wastewarrior.models.SurpriseBag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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


        val chipGroup = binding.chipGroup

        val tagList: List<String> = mutableListOf("Coffee", "Chicken", "Dessert", "Barbeque")

        if (tagList.isNotEmpty()){
            binding.nocategory.visibility = View.GONE
            binding.chipGroup.visibility = View.VISIBLE
        for (tag in tagList) {
            val chip = Chip(this)
            chip.text = tag
            chip.setChipBackgroundColorResource(R.color.teal_200)
            chip.setChipStrokeColorResource(R.color.teal_700)
            chip.chipStrokeWidth = resources.getDimension(com.google.android.material.R.dimen.m3_card_stroke_width)
            chip.setTextColor(resources.getColor(R.color.white))

            // Add a click listener to handle chip click events
            chip.setOnClickListener { view: View? ->
                // Handle chip click here
                val selectedTag = chip.text.toString()
            }
            chipGroup.addView(chip)
        }
        }


        // Retrieve surprise bags from Firestore
        retrieveSurpriseBags()
        binding.addChipButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
        })
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
                                val category = it["price"]
                                SurpriseBag(name, quantity, isFavourite, price,category.toString() )
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
