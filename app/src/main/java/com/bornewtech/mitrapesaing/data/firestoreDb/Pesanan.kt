package com.bornewtech.mitrapesaing.data.firestoreDb

data class PesananItem(
    val pembeliId: String?,
    val namaPembeli: String, // Ganti dengan tipe data yang sesuai dengan nama pembeli
    val namaProduk: String,
    val productQuantity: Int
)

data class Pesanan(
    val cartItems: List<PesananItem>? = null,
    val idTransaksi: String? = null,
    val status: String? = null,
    val timestamp: Long = 0
)
