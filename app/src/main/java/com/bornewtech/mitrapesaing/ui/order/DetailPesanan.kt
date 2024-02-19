package com.bornewtech.mitrapesaing.ui.order

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.google.firebase.firestore.FirebaseFirestore

class DetailPesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterDetailPesanan
    private lateinit var tvTotalPrice: TextView
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)

        recyclerView = findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterDetailPesanan(mutableListOf())
        recyclerView.adapter = adapter

        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        currentUserID = intent.getStringExtra("userID") ?: ""

        // Mendapatkan data dari Firestore
        getFirestoreData()
    }

    private fun getFirestoreData() {
        if (currentUserID.isNotBlank()) {
            val firestore = FirebaseFirestore.getInstance()

            firestore.collection("Transaksi")
                .document(currentUserID)
                .collection("cartItems")
                .get()
                .addOnSuccessListener { documents ->
                    val cartItemList = mutableListOf<CartItem>()
                    var totalHarga = 0.0

                    for (document in documents) {
                        val data = document.toObject(CartItem::class.java)
                        cartItemList.add(data)
                        totalHarga += data.productPrice * data.productQuantity.toDouble()
                    }

                    adapter.updateData(cartItemList)
                    tvTotalPrice.text = "Rp ${totalHarga.toInt()}"
                }
                .addOnFailureListener { exception ->
                    Log.e("DetailPesanan", "Error getting cartItems", exception)
                }
        } else {
            Log.e("DetailPesanan", "CurrentUserID is null or empty")
        }
    }
}




//package com.bornewtech.mitrapesaing.ui.order
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
//import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem
//import com.google.firebase.firestore.FirebaseFirestore
//
//class DetailPesanan : AppCompatActivity() {
//
//    private lateinit var adapter: AdapterDetailPesanan
//    private lateinit var dataList: MutableList<CartItem>
//    private lateinit var pembeliId: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//
//        // Inisialisasi RecyclerView dan Adapter
//        val recyclerView: RecyclerView = findViewById(R.id.rvCart)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        dataList = mutableListOf()
//        adapter = AdapterDetailPesanan(dataList)
//        recyclerView.adapter = adapter
//
//        // Mendapatkan pembeliId dari intent (riwayatTransaksi)
//        pembeliId = intent.getStringExtra("pembeliId") ?: ""
//
//        // Memanggil fungsi untuk mendapatkan data Keranjang menggunakan pembeliId dari riwayatTransaksi
//        getCartData(pembeliId)
//    }
//
//    private fun getCartData(pembeliId: String) {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("Keranjang")
//            .document(pembeliId) // Gunakan pembeliId dari riwayatTransaksi
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    val cartData = document.data
//                    if (cartData != null) {
//                        val cartItems = cartData["cartItems"] as List<HashMap<String, Any>>
//                        // Memperbarui RecyclerView dengan data cartItems
//                        updateRecyclerView(cartItems)
//                        Log.d(TAG, "Data cartItems berhasil diambil: $cartItems")
//                    } else {
//                        Log.d(TAG, "Dokumen Keranjang tidak berisi data cartItems")
//                    }
//                } else {
//                    Log.d(TAG, "Dokumen Keranjang tidak ditemukan")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "getCartData gagal", exception)
//            }
//    }
//
//    private fun updateRecyclerView(cartItemList: List<HashMap<String, Any>>) {
//        // Konversi HashMap ke List<CartItem>
//        val convertedList = cartItemList.map { map ->
//            CartItem(
//                imageUrl = map["imageUrl"] as String,
//                pedagangId = map["pedagangId"] as String,
//                pembeliId = map["pembeliId"] as String,
//                productName = map["productName"] as String,
//                productPrice = (map["productPrice"] as Long).toInt(), // Ubah ke Double
//                productQuantity = (map["productQuantity"] as Long).toInt(), // Ubah ke Int
//                productId = map["productId"] as String // Biarkan sebagai String
//            )
//        }
//        // Memperbarui dataList dengan data cartItemList
//        dataList.clear()
//        dataList.addAll(convertedList)
//        // Memberi tahu adapter bahwa data telah berubah
//        adapter.notifyDataSetChanged()
//    }
//
//    companion object {
//        private const val TAG = "DetailPesanan"
//    }
//}