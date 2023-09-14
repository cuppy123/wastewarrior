package com.example.wastewarrior.models

data class Category(
    val name: String
){
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
        )
    }
}