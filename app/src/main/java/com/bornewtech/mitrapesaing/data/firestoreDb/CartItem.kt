package com.bornewtech.mitrapesaing.data.firestoreDb

import java.io.Serializable


data class CartItem(
    val imageUrl: String = "",
    val pedagangId: String = "",
    val pembeliId: String = "",
    val productId: String = "",
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productQuantity: Int = 0,
) : Serializable
