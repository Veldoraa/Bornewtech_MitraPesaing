package com.bornewtech.mitrapesaing.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.bornewtech.mitrapesaing.data.firestoreDb.TransaksiDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailPesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var tvStatus: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var pembeliId: String // Menyimpan pembeliId dari intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)

        // Inisialisasi TextView dan RecyclerView
        tvStatus = findViewById(R.id.tvStatus)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        recyclerView = findViewById(R.id.rvCartDetail)

        val pembeliId = intent.getStringExtra("pembeliId")
        pembeliId?.let {
            // Simpan pembeliId dari intent
            this.pembeliId = pembeliId
            // Memuat data dari Firestore
            getDataFromFirestore()
        }

        // Inisialisasi adapter RecyclerView
        adapterDetailPesanan = AdapterDetailPesanan()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailPesanan)
            adapter = adapterDetailPesanan
        }

        val btnGetData = findViewById<Button>(R.id.btnGetData)
        btnGetData.setOnClickListener {
            // Panggil fungsi untuk mendapatkan data dari Firestore saat tombol ditekan
            getDataFromFirestore()
        }
    }

    private fun getDataFromFirestore() {
        firestore.collection("Transaksi")
            .document(pembeliId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val transaksi = document.toObject(TransaksiDetail::class.java)
                    transaksi?.let { data ->
                        val cartItems = data.cartItems
                        if (cartItems != null) {
                            // Jika ada cartItems, update adapter RecyclerView
                            adapterDetailPesanan.setData(cartItems)
                        } else {
                            Log.d("DetailPesanan", "Cart items not found")
                        }
                    }
                } else {
                    Log.d("DetailPesanan", "Transaksi document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DetailPesanan", "Error fetching transaksi data", exception)
            }
    }
}


//class DetailPesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private lateinit var tvStatus: TextView
//    private lateinit var tvTotalPrice: TextView
//    private lateinit var pembeliId: String // tambahkan variabel pembeliId untuk menyimpan pembeliId dari intent
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//
//        // Inisialisasi TextView dan RecyclerView
//        tvStatus = findViewById(R.id.tvStatus)
//        tvTotalPrice = findViewById(R.id.tvTotalPrice)
//        recyclerView = findViewById(R.id.rvCartDetail)
//
//        val orderan = intent.getSerializableExtra("orderan") as? Orderan
//        orderan?.let { order ->
//            val status = order.status
//            val totalHarga = order.totalHarga
//            pembeliId = order.pembeliId // Simpan pembeliId dari intent
//
//            tvStatus.text = status
//            tvTotalPrice.text = "Rp $totalHarga"
//        }
//
//        adapterDetailPesanan = AdapterDetailPesanan()
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@DetailPesanan)
//            adapter = adapterDetailPesanan
//        }
//
//        val btnGetData = findViewById<Button>(R.id.btnGetData)
//        btnGetData.setOnClickListener {
//            getDataFromFirestore() // Panggil fungsi untuk mendapatkan data dari Firestore saat tombol ditekan
//        }
//    }
//
//    private fun getDataFromFirestore() {
//        firestore.collection("Transaksi")
//            .document(pembeliId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null && document.exists()) {
//                    val transaksi = document.toObject(TransaksiDetail::class.java)
//                    transaksi?.let { data ->
//                        val cartItems = data.cartItems
//                        if (cartItems != null) {
//                            adapterDetailPesanan.setData(cartItems)
//                        } else {
//                            Log.d("DetailPesanan", "Cart items not found")
//                        }
//                    }
//                } else {
//                    Log.d("DetailPesanan", "Transaksi document not found")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("DetailPesanan", "Error fetching transaksi data", exception)
//            }
//    }
//}






//class DetailPesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AdapterDetailPesanan
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var pembeliId: String
//    private lateinit var idTransaksi: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//
//        recyclerView = findViewById(R.id.rvCart)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = AdapterDetailPesanan()
//        recyclerView.adapter = adapter
//
//        firestore = FirebaseFirestore.getInstance()
//        pembeliId = intent.getStringExtra("pembeliId") ?: ""
//        idTransaksi = intent.getStringExtra("idTransaksi") ?: ""
//
//        // Muat detail pesanan
//        Log.d("DetailPesanan", "Menerima idTransaksi: $idTransaksi dan pembeliId: $pembeliId dari Pesanan")
//        loadDetailPesanan()
//    }
//
//    private fun loadDetailPesanan() {
//        firestore.collection("Transaksi")
//            .document(idTransaksi)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val transaksiData = document.toObject(TransaksiDetail::class.java)
//                    if (transaksiData != null) {
//                        // Memeriksa apakah idTransaksi cocok
//                        if (transaksiData.idTransaksi == idTransaksi) {
//                            // Ambil cartItems dari data transaksi
//                            val cartItems = transaksiData.cartItems?.filter { it.pembeliId == pembeliId }
//                            if (cartItems != null) {
//                                // Jika ingin menampilkan status dan totalHarga, tambahkan ke RecyclerView
//                                val detailPesananItems = mutableListOf<CartItem>()
//                                detailPesananItems.addAll(cartItems)
//                                // Tambahkan status dan totalHarga ke RecyclerView
//                                val statusItem = CartItem("", "", 0.0, 0, "", "", transaksiData.status)
//                                val totalHargaItem = CartItem("", "Total Harga", transaksiData.totalHarga, 0, "", "", "")
//                                detailPesananItems.add(statusItem)
//                                detailPesananItems.add(totalHargaItem)
//                                adapter.setData(detailPesananItems)
//                            }
//                        }
//                    }
//                }
//            }
//    }
//}


//class DetailPesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AdapterDetailPesanan
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var pembeliId: String
//    private lateinit var pedagangId: String
//    private lateinit var idTransaksi: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//
//        recyclerView = findViewById(R.id.rvCart)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = AdapterDetailPesanan()
//        recyclerView.adapter = adapter
//
//        firestore = FirebaseFirestore.getInstance()
//        pembeliId = intent.getStringExtra("userID") ?: ""
//        idTransaksi = intent.getStringExtra("idTransaksi") ?: ""
//
//        // Dapatkan pedagangId dari Firebase Authentication
//        getPedagangIdAndLoadData()
//    }
//
//    private fun getPedagangIdAndLoadData() {
//        val firebaseAuth = FirebaseAuth.getInstance()
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            // Pengguna masuk
//            pedagangId = currentUser.uid
//            Log.d("DetailPesanan", "Pedagang ID: $pedagangId")
//            // Sekarang Anda bisa memanggil getTransaksiDetail di sini dengan pedagangId yang didapatkan
//            getTransaksiDetail(idTransaksi, pedagangId)
//        } else {
//            // Pengguna belum masuk atau sesi masuk sudah berakhir
//            Log.d("DetailPesanan", "Tidak ada pengguna yang masuk")
//            // Lakukan penanganan kesalahan atau tindakan yang sesuai di sini
//        }
//    }
//    private fun getTransaksiDetail(idTransaksi: String, pembeliId: String) {
//        firestore.collection("Transaksi")
//            .document(idTransaksi)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val transaksiData = document.toObject(TransaksiDetail::class.java)
//                    if (transaksiData != null) {
//                        // Memeriksa apakah idTransaksi cocok
//                        if (transaksiData.idTransaksi == idTransaksi) {
//                            // Ambil cartItems dari data transaksi
//                            val cartItems = transaksiData.cartItems?.filter { it.pembeliId == pembeliId }
//                            if (cartItems != null) {
//                                adapter.setData(cartItems)
//                            }
//                            Log.d("DetailPesanan", "Transaksi berhasil didapatkan: $transaksiData")
//                        } else {
//                            Log.d("DetailPesanan", "idTransaksi tidak cocok: ${transaksiData.idTransaksi} != $idTransaksi")
//                        }
//                    } else {
//                        Log.d("DetailPesanan", "Data transaksi null")
//                    }
//                } else {
//                    Log.d("DetailPesanan", "Tidak ada transaksi dengan idTransaksi: $idTransaksi")
//                    adapter.setData(emptyList())
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("DetailPesanan", "Gagal mendapatkan transaksi", exception)
//                adapter.setData(emptyList())
//            }
//    }
//
//
//
////    private fun getTransaksiDetail(idTransaksi: String, pedagangId: String) {
////        firestore.collection("Transaksi")
////            .document(idTransaksi)
////            .get()
////            .addOnSuccessListener { document ->
////                if (document.exists()) {
////                    val transaksiData = document.toObject(TransaksiDetail::class.java)
////                    if (transaksiData != null) {
////                        // Pastikan idTransaksi di firestore sama dengan yang diterima dari intent
////                        if (transaksiData.idTransaksi == idTransaksi) {
////                            // Ambil cartItems dari data transaksi yang memiliki pedagangId yang sesuai dengan pedagang yang sedang login
////                            val cartItems = transaksiData.cartItems?.filter { it.pedagangId == pedagangId }
////                            // Tampilkan data cartItems ke dalam RecyclerView
////                            if (cartItems != null) {
////                                adapter.setData(cartItems)
////                            }
////                            // Tambahkan logcat untuk memeriksa data transaksi
////                            Log.d("DetailPesanan", "Transaksi berhasil didapatkan: $transaksiData")
////                        } else {
////                            Log.d("DetailPesanan", "idTransaksi tidak cocok: ${transaksiData.idTransaksi} != $idTransaksi")
////                        }
////                    } else {
////                        Log.d("DetailPesanan", "Data transaksi null")
////                    }
////                } else {
////                    Log.d("DetailPesanan", "Tidak ada transaksi dengan idTransaksi: $idTransaksi")
////                    // Tampilkan pesan kesalahan kepada pengguna jika transaksi tidak ditemukan
////                    adapter.setData(emptyList())
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.e("DetailPesanan", "Gagal mendapatkan transaksi", exception)
////                // Tampilkan pesan kesalahan kepada pengguna jika terjadi kesalahan dalam mengambil data transaksi
////                adapter.setData(emptyList())
////            }
////    }
//}
