package com.bornewtech.mitrapesaing.data.firestoreDb

import java.io.Serializable

data class Orderan(
    val idTransaksi: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val alamatLengkap: String = "",
    val namaPembeli: String = "",
    val pedagangId: String = "",
    val pembeliId: String = "",
    val status: String = "",
    val totalHarga: Int = 0,
//    val cartItems: List<CartItem>? = null // Tambahkan properti cartItems
) : Serializable
