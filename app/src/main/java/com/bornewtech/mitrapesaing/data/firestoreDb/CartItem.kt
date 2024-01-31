package com.bornewtech.mitrapesaing.data.firestoreDb

data class CartItem(
    val productId: String?,
    val productName: String,
    val productPrice: Double,
    var productQuantity: Int,
    var pedagangId: String?,
    var pembeliId: String?
)
data class Transaction(
    val idTransaksi: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val jumlahHarga: Int = 0,
    val cartItems: List<CartItem>? = null,
    val timestamp: Long = 0
)