package com.bornewtech.mitrapesaing.data.firestoreDb

data class Products(
    // Other fields...
    val productList: List<ProductItem>? = null
)

data class ProductItem(
    val produkStok: String? = null,
    val produkNama: String? = null,
    val produkKategori: String? = null,
    val produkSatuan: String? = null,
    val produkHarga: String? = null
)
