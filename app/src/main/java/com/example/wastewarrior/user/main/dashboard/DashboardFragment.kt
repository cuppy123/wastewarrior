package com.example.wastewarrior.user.main.dashboard

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wastewarrior.R
import com.example.wastewarrior.databinding.FragmentDashboardBinding
import com.example.wastewarrior.models.Restaurant
import com.example.wastewarrior.models.SurpriseBag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class DashboardFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var mMap: GoogleMap

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val restaurantList = mutableListOf<Restaurant>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val db = FirebaseFirestore.getInstance()
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { querySnapshot ->


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

                val mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve restaurants
                Toast.makeText(context,"Error:${exception?.message.toString()}",Toast.LENGTH_LONG).show()
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Clear any existing markers
        mMap.clear()
        val drawable = resources.getDrawable(R.drawable.custom_marker_icon) // Replace with your drawable resource
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        for (restaurant in restaurantList) {
            val restaurantLatLng = LatLng(restaurant.address["latitude"] as Double,
                restaurant.address["longitude"] as Double
            )

            // Customize marker options
            val markerOptions = MarkerOptions()
                .position(restaurantLatLng)
                .title(restaurant.name)
                .snippet("Address: ${restaurant.address}") // Customize this
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)) // Customize the marker icon

            mMap.addMarker(markerOptions)
        }

        // Set the camera position to show all markers
        val boundsBuilder = LatLngBounds.builder()
        for (restaurant in restaurantList) {
            boundsBuilder.include( LatLng(restaurant.address["latitude"] as Double,
                restaurant.address["longitude"] as Double
            ))
        }
        val bounds = boundsBuilder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        mMap.moveCamera(cameraUpdate)

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }
    }
}