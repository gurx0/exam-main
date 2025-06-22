package com.example.myapplication

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class Service(
    val id: Int,
    val name: String,
    val price: Double,
    val duration: String
)

data class CartItem(
    val service: Service,
    var quantity: MutableState<Int> = mutableStateOf(1)
)