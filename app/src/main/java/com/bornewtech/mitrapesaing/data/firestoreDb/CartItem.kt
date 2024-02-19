package com.bornewtech.mitrapesaing.data.firestoreDb

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val productId: String?,
    val productName: String,
    val productPrice: Double,
    var productQuantity: Int,
    var pedagangId: String?,
    var pembeliId: String?,
    val imageUrl: String? // Tambahkan properti imageUrl untuk menyimpan URL gambar produk
) : Parcelable
