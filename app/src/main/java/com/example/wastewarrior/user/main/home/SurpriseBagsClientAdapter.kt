package com.example.wastewarrior.user.main.home

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.wastewarrior.R
import com.example.wastewarrior.admin.SurpriseBagsActivity
import com.example.wastewarrior.models.SurpriseBag
import com.example.wastewarrior.user.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SurpriseBagsClientAdapter(private val surpriseBags: List<SurpriseBag>) :
    RecyclerView.Adapter<SurpriseBagsClientAdapter.SurpriseBagViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    inner class SurpriseBagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(surpriseBag: SurpriseBag) {
            val itemNameTextView = itemView.findViewById<TextView>(R.id.itemName)
            val itemQuantityTextView = itemView.findViewById<TextView>(R.id.itemQuantity)
            val itemPriceTextView = itemView.findViewById<TextView>(R.id.itemPrice)
            itemView.findViewById<CardView>(R.id.orderCard).setOnClickListener {
                //updateSurpriseBagByName(surpriseBag,itemView.context)
                addOrderToFirestore(surpriseBag,itemView.context)
                addOrderToFirestore(surpriseBag,itemView.context)
            }

            itemView.findViewById<ImageView>(R.id.imageView4).setOnClickListener {
                addFariteToFirestore(surpriseBag,itemView.context)
            }
            itemNameTextView.text = surpriseBag.name
            itemQuantityTextView.text = surpriseBag.quantity.toString()+" left"
            itemPriceTextView.text = "£${surpriseBag.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurpriseBagViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_surprise_bag_client, parent, false)
        return SurpriseBagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SurpriseBagViewHolder, position: Int) {
        holder.bind(surpriseBags[position])
    }

    override fun getItemCount(): Int = surpriseBags.size

    private fun updateSurpriseBagByName(updatedSurpriseBag: SurpriseBag,context:Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("restaurants").document(it)

            // Retrieve the existing surprises list and update the specific surprise bag by name
            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("surprises") as MutableList<HashMap<String, Any>>?
                    surprises?.let {
                        // Find the index of the surprise bag to be updated based on its name
                        val current:SurpriseBag?=null
                        val indexToUpdate = it.indexOfFirst { surprise ->
                            surprise["name"] == updatedSurpriseBag.name
                        }

                        if (indexToUpdate != -1) {
                            val currentQuantity = it[indexToUpdate]["quantity"] as Long
                            it[indexToUpdate]["quantity"] = currentQuantity - 1

                            val current=it
                            restaurantRef.update("surprises", it)
                                .addOnSuccessListener {
                                    showToast("Surprise bag updated successfully",context)
                                    addOrderToFirestore(updatedSurpriseBag,context)
                                    addOrderToRestaurantFirestore(updatedSurpriseBag,context)
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                }
                                .addOnFailureListener { exception ->
                                    showToast("Failed to update surprise bag",context)
                                }
                        } else {
                            showToast("Surprise bag not found",context)
                        }
                    }
                } else {
                    showToast("Restaurant document not found",context)
                }
            }.addOnFailureListener { exception ->
                showToast("Error: ${exception.message}",context)
            }
        }
    }

    private fun showToast(message: String,context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    private fun addOrderToFirestore(surpriseBag: SurpriseBag,context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("users").document(it)

            // Retrieve the existing surprises list and update it

            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("orders") as MutableList<HashMap<String, Any>>?
                    surprises?.add(surpriseBag.toHashMap())
                    surprises?.let {
                        restaurantRef.update("surprises", it)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                } else {
                }
            }.addOnFailureListener { exception ->
                showToast("Error: ${exception.message}",context)
            }
        }
    }

    private fun addFariteToFirestore(surpriseBag: SurpriseBag,context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("users").document(it)

            // Retrieve the existing surprises list and update it

            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("farites") as MutableList<HashMap<String, Any>>?
                    surprises?.add(surpriseBag.toHashMap())
                    surprises?.let {
                        restaurantRef.update("farites", it)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                } else {
                }
            }.addOnFailureListener { exception ->
                showToast("Error: ${exception.message}",context)
            }
        }
    }
    private fun addOrderToRestaurantFirestore(surpriseBag: SurpriseBag,context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let {
            val restaurantRef = db.collection("restaurants").document(it)

            // Retrieve the existing surprises list and update it
            restaurantRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val surprises = documentSnapshot.get("orders") as MutableList<HashMap<String, Any>>?
                    surprises?.add(surpriseBag.toHashMap())
                    surprises?.let {
                        restaurantRef.update("surprises", it)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { exception ->
                            }
                    }
                } else {
                }
            }.addOnFailureListener { exception ->
                showToast("Error: ${exception.message}",context)
            }
        }
    }

}
