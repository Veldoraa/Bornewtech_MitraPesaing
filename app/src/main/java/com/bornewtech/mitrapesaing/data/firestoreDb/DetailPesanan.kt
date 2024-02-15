package com.bornewtech.mitrapesaing.data.firestoreDb

import java.io.Serializable

data class DetailPesanan(
    val idTransaksi: String? = null,
    val status: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val jumlahHarga: Int = 0,
    val cartItems: List<CartItem>? = null,
    val timestamp: Long = 0
)  : Serializable
