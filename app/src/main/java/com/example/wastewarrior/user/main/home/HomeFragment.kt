package com.example.wastewarrior.user.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wastewarrior.databinding.FragmentHomeBinding
import com.example.wastewarrior.models.Restaurant
import com.example.wastewarrior.models.SurpriseBag
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var restaurantAdapter: RestaurantAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db.collection("restaurants")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val restaurantList = mutableListOf<Restaurant>()

                for (document in querySnapshot.documents) {
                    val restaurantData = document.data
                    val name = restaurantData?.get("name") as String
                    val address = restaurantData["location"] as HashMap<String, Any>
                    // Add more properties as needed

                    // Retrieve surprise bags from the restaurantData and convert to SurpriseBag objects
                    val surprisesData = restaurantData["surprises"] as List<HashMap<String, Any>>?
                    val surpriseBags = surprisesData?.map { surprise ->
                        val surpriseName = surprise["name"] as String
                        val quantity = (surprise["quantity"] as Long).toInt()
                        val isFavourite = surprise["isFavourite"] as Boolean
                        val price = surprise["price"] as Double
                        SurpriseBag(surpriseName, quantity, isFavourite, price)
                    } ?: emptyList()

                    // Create a Restaurant object with the fetched data
                    val restaurant = Restaurant(name, address, surpriseBags)
                    restaurantList.add(restaurant)
                }
                restaurantAdapter = RestaurantAdapter(restaurantList)
                binding.recyclerView.adapter = restaurantAdapter
                Log.i("rest",restaurantList.toString())
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve restaurants
            }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}