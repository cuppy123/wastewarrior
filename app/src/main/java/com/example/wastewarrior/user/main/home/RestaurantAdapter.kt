package com.example.wastewarrior.user.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wastewarrior.R
import com.example.wastewarrior.models.Restaurant

class RestaurantAdapter(private val restaurantList: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurantName: TextView = itemView.findViewById(R.id.textRestaurantName)
        val recyclerViewSurpriseBags: RecyclerView = itemView.findViewById(R.id.recyclerViewSurpriseBags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.restaurantName.text = restaurant.name
        //holder.restaurantAddress.text = restaurant.address['latitude']

        // Set up the nested RecyclerView for surprise bags
        val surpriseBagAdapter = SurpriseBagsClientAdapter(restaurant.surprises)
        holder.recyclerViewSurpriseBags.adapter = surpriseBagAdapter
    }

    override fun getItemCount(): Int = restaurantList.size
}
