package com.example.wastewarrior.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wastewarrior.R
import com.example.wastewarrior.models.SurpriseBag

class SurpriseBagAdapter(private val surpriseBags: List<SurpriseBag>) :
    RecyclerView.Adapter<SurpriseBagAdapter.SurpriseBagViewHolder>() {

    inner class SurpriseBagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(surpriseBag: SurpriseBag) {
            val itemNameTextView = itemView.findViewById<TextView>(R.id.itemName)
            val itemQuantityTextView = itemView.findViewById<TextView>(R.id.itemQuantity)
            val itemIsFavouriteTextView = itemView.findViewById<TextView>(R.id.itemIsFavourite)
            val itemPriceTextView = itemView.findViewById<TextView>(R.id.itemPrice)

            itemNameTextView.text = surpriseBag.name
            itemQuantityTextView.text = surpriseBag.quantity.toString()
            itemIsFavouriteTextView.text = if (surpriseBag.isFavourite) "Favourite" else "Not Favourite"
            itemPriceTextView.text = "Price: ${surpriseBag.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurpriseBagViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_surprise_bag, parent, false)
        return SurpriseBagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SurpriseBagViewHolder, position: Int) {
        holder.bind(surpriseBags[position])
    }

    override fun getItemCount(): Int = surpriseBags.size
}
