package com.bornewtech.marketplacepesaing.data.model

data class ListBarang(
    var user_id : Int?,
    var barang_id : Int?,
    var gambar_barang : String?,
    var nama_barang : String? = null,
    var deskripsi : String? = null,
    var kategori_barang : String? = null,
    var stok_barang : Int? = null,
    var harga_barang : Int? = null,
    var satuan_barang : String? = null
)
