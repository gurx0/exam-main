package com.example.myapplication

data class Service(
    val id: Int,
    val name: String,
    val price: Double,
    val duration: String
)

data class CartItem(
    val service: Service,
    var quantity: Int
)