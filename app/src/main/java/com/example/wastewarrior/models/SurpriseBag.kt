package com.example.wastewarrior.models

data class SurpriseBag(
    val name: String,
    val quantity: Int,
    val isFavourite: Boolean,
    val price: Double
){
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "quantity" to quantity,
            "isFavourite" to isFavourite,
            "price" to price
        )
    }
}

