package com.bornewtech.mitrapesaing.data.firestoreDb

import java.io.Serializable

data class TransaksiDetail(
    val idTransaksi: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0,
    val status: String = "",
    val totalHarga: Double = 0.0,
    val pembeliId: String = "",
    val pedagangId: String = "",
    val cartItems: List<CartItem>? = null // Tambahkan properti cartItems
) : Serializable
