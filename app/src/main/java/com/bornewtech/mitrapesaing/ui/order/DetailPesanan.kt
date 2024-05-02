//package com.bornewtech.mitrapesaing.ui.order
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
//import com.google.firebase.database.*
//import com.google.firebase.firestore.FirebaseFirestore
//import android.net.Uri
//import com.bornewtech.mitrapesaing.data.firestoreDb.TransaksiDetail
//import com.google.firebase.auth.FirebaseAuth
//
//class DetailPesanan : AppCompatActivity() {
//
//    private val TAG = "DetailPesanan"
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private lateinit var tvStatus: TextView
//    private lateinit var tvTotalPrice: TextView
//    private lateinit var pembeliId: String
//    private lateinit var pedagangId: String
//    private lateinit var idTransaksi: String // Pastikan untuk menyimpan idTransaksi
//    private val database = FirebaseDatabase.getInstance()
//
//    private var latitude: Double? = null
//    private var longitude: Double? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//        supportActionBar?.hide()
//
//        tvStatus = findViewById(R.id.tvStatus)
//        tvTotalPrice = findViewById(R.id.tvTotalPrice)
//        recyclerView = findViewById(R.id.rvCartDetail)
//
//        pembeliId = intent.getStringExtra("pembeliId") ?: ""
//        pedagangId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//        idTransaksi = intent.getStringExtra("idTransaksi") ?: "" // Ambil idTransaksi
//
//        val status = intent.getStringExtra("status") ?: ""
//        val totalHarga = intent.getDoubleExtra("totalHarga", 0.0) // Ambil totalHarga
//
//        Log.d(TAG, "Data diterima: pembeliId=$pembeliId, idTransaksi=$idTransaksi, totalHarga=$totalHarga, status=$status")
//
//        tvStatus.text = status
//        tvTotalPrice.text = "Rp $totalHarga"
//
//        adapterDetailPesanan = AdapterDetailPesanan()
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapterDetailPesanan
//
//        val btnTolakPesanan = findViewById<Button>(R.id.btnTolakPesanan)
//        btnTolakPesanan.setOnClickListener {
//            val intent = Intent(this, TolakPesanan::class.java)
//            intent.putExtra("pembeliId", pembeliId)
//            intent.putExtra("idTransaksi", idTransaksi) // Pastikan idTransaksi dikirim
//            startActivity(intent)
//        }
//
//        val btnTerima = findViewById<Button>(R.id.btnTerima)
//        btnTerima.setOnClickListener {
//            latitude?.let { lat ->
//                longitude?.let { lng ->
//                    val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
//                    val intent = Intent(Intent.ACTION_VIEW, uri)
//                    startActivity(intent)
//                }
//            }
//        }
//
//        getDataFromFirestore(idTransaksi) // Gunakan idTransaksi untuk mengambil data transaksi
//        fetchLocationData(pembeliId)
//    }
//
//    private fun getDataFromFirestore(idTransaksi: String) {
//        firestore.collection("Transaksi").document(idTransaksi).get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val transaksi = document.toObject(TransaksiDetail::class.java)
//                    adapterDetailPesanan.setData(transaksi?.cartItems ?: emptyList())
//                } else {
//                    Log.d(TAG, "Dokumen transaksi tidak ditemukan")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Kesalahan saat mengambil data transaksi", e)
//            }
//    }
//
//    private fun fetchLocationData(pembeliId: String) {
//        val locationRef = database.getReference("userLocations/pembeli/$pembeliId")
//
//        locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
//                longitude = dataSnapshot.child("longitude").getValue(Double::class.java)
//
//                if (latitude != null && longitude != null) {
//                    Log.d(TAG, "Latitude: $latitude, Longitude: $longitude")
//                } else {
//                    Log.d(TAG, "Data latitude atau longitude tidak ditemukan")
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e(TAG, "Kesalahan saat mengambil data lokasi", databaseError.toException())
//            }
//        })
//    }
//}





package com.bornewtech.mitrapesaing.ui.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import com.bornewtech.mitrapesaing.data.firestoreDb.TransaksiDetail

class DetailPesanan : AppCompatActivity() {

    private val TAG = "DetailPesanan"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var tvStatus: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var pembeliId: String
    private lateinit var pedagangId: String
    private val database = FirebaseDatabase.getInstance()

    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var idTransaksi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)
        supportActionBar?.hide()

        tvStatus = findViewById(R.id.tvStatus)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        recyclerView = findViewById(R.id.rvCartDetail)

        pembeliId = intent.getStringExtra("pembeliId") ?: ""
        pedagangId = intent.getStringExtra("pedagangId") ?: ""
        idTransaksi = intent.getStringExtra("idTransaksi") ?: "" // Ambil idTransaksi

        val status = intent.getStringExtra("status") ?: ""
        val totalHarga = intent.getIntExtra("totalHarga", 0)

        Log.d(TAG, "Data diterima: pembeliId=$pembeliId, pedagangId=$pedagangId, idTransaksi=$idTransaksi, totalHarga=$totalHarga, status=$status")

        fetchLocationData(pembeliId)

        tvStatus.text = status
        tvTotalPrice.text = "Rp $totalHarga"

        adapterDetailPesanan = AdapterDetailPesanan()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterDetailPesanan

        val btnTolakPesanan = findViewById<Button>(R.id.btnTolakPesanan)
        btnTolakPesanan.setOnClickListener {
            val intent = Intent(this, TolakPesanan::class.java)
            intent.putExtra("pembeliId", pembeliId)
            intent.putExtra("idTransaksi", idTransaksi) // Pastikan idTransaksi dikirim
            startActivity(intent)
        }

        val btnTerima = findViewById<Button>(R.id.btnTerima)
        btnTerima.setOnClickListener {
            latitude?.let { lat ->
                longitude?.let { lng ->
                    val uri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                }
            }
        }

        getDataFromFirestore()
    }

    private fun getDataFromFirestore() {
        firestore.collection("Transaksi").document(pembeliId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val transaksi = document.toObject(TransaksiDetail::class.java)
                    adapterDetailPesanan.setData(transaksi?.cartItems ?: emptyList())
                } else {
                    Log.d(TAG, "Dokumen transaksi tidak ditemukan")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Kesalahan saat mengambil data transaksi", e)
            }
    }

    private fun fetchLocationData(pembeliId: String) {
        val locationRef = database.getReference("userLocations/pembeli/$pembeliId")

        locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                longitude = dataSnapshot.child("longitude").getValue(Double::class.java)

                if (latitude != null && longitude != null) {
                    Log.d(TAG, "Latitude: $latitude, Longitude: $longitude")
                } else {
                    Log.d(TAG, "Data latitude atau longitude tidak ditemukan")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Kesalahan saat mengambil data lokasi", databaseError.toException())
            }
        })
    }
}







//package com.bornewtech.mitrapesaing.ui.order
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
//import com.bornewtech.mitrapesaing.data.firestoreDb.TransaksiDetail
//import com.google.firebase.firestore.FirebaseFirestore
//
//class DetailPesanan : AppCompatActivity() {
//
//    private val TAG = "DetailPesanan" // TAG untuk logcat
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private lateinit var tvStatus: TextView
//    private lateinit var tvTotalPrice: TextView
//    private lateinit var pembeliId: String // Simpan pembeliId dari intent
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//        supportActionBar?.hide()
//
//        // Inisialisasi TextView dan RecyclerView
//        tvStatus = findViewById(R.id.tvStatus)
//        tvTotalPrice = findViewById(R.id.tvTotalPrice)
//        recyclerView = findViewById(R.id.rvCartDetail)
//
//        val pembeliId = intent.getStringExtra("pembeliId")
//        val status = intent.getStringExtra("status")
//        val totalHarga = intent.getDoubleExtra("totalHarga", 0.0)
//
//        // Log data yang diterima melalui Intent
//        Log.d(TAG, "Data diterima: pembeliId=$pembeliId, status=$status, totalHarga=$totalHarga")
//
//        pembeliId?.let {
//            this.pembeliId = it
//            getDataFromFirestore()
//        }
//
//        tvStatus.text = status
//        tvTotalPrice.text = "Rp $totalHarga"
//
//        // Inisialisasi adapter RecyclerView
//        adapterDetailPesanan = AdapterDetailPesanan()
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapterDetailPesanan
//
//        val btnTolakPesanan = findViewById<Button>(R.id.btnTolakPesanan)
//        btnTolakPesanan.setOnClickListener {
//            startActivity(Intent(this, TolakPesanan::class.java))
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
//                    Log.d(TAG, "Data transaksi: $transaksi") // Log detail transaksi
//                    transaksi?.cartItems?.let { cartItems ->
//                        adapterDetailPesanan.setData(cartItems)
//                    } ?: run {
//                        Log.d(TAG, "Cart items tidak ditemukan")
//                    }
//                } else {
//                    Log.d(TAG, "Dokumen transaksi tidak ditemukan")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "Kesalahan saat mengambil data transaksi", exception)
//            }
//    }
//}



//package com.bornewtech.mitrapesaing.ui.order
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
//import com.bornewtech.mitrapesaing.data.firestoreDb.TransaksiDetail
//import com.google.firebase.firestore.FirebaseFirestore
//
//class DetailPesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private lateinit var tvStatus: TextView
//    private lateinit var tvTotalPrice: TextView
//    private lateinit var pembeliId: String // Menyimpan pembeliId dari intent
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detail_pesanan)
//        supportActionBar?.hide()
//
//        // Inisialisasi TextView dan RecyclerView
//        tvStatus = findViewById(R.id.tvStatus)
//        tvTotalPrice = findViewById(R.id.tvTotalPrice)
//        recyclerView = findViewById(R.id.rvCartDetail)
//
//        val pembeliId = intent.getStringExtra("pembeliId")
//        val status = intent.getStringExtra("status")
//        val totalHarga = intent.getDoubleExtra("totalHarga", 0.0)
//
//        // Log data yang diterima melalui Intent
//        Log.d("DetailPesanan", "Data received: pembeliId=$pembeliId, status=$status, totalHarga=$totalHarga")
//
//        pembeliId?.let {
//            // Simpan pembeliId dari intent
//            this.pembeliId = pembeliId
//            // Memuat data dari Firestore
//            getDataFromFirestore()
//        }
//
//        // Tampilkan status dan total harga
//        tvStatus.text = status
//        tvTotalPrice.text = "Rp $totalHarga"
//
//        // Inisialisasi adapter RecyclerView
//        adapterDetailPesanan = AdapterDetailPesanan()
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@DetailPesanan)
//            adapter = adapterDetailPesanan
//        }
//
//        val btnGetData = findViewById<Button>(R.id.btnTolakPesanan)
//        btnGetData.setOnClickListener {
//            startActivity(Intent(this, TolakPesanan::class.java))
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
//                            // Jika ada cartItems, update adapter RecyclerView
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
//
//
//
////class DetailPesanan : AppCompatActivity() {
////
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var adapterDetailPesanan: AdapterDetailPesanan
////    private val firestore = FirebaseFirestore.getInstance()
////    private lateinit var tvStatus: TextView
////    private lateinit var tvTotalPrice: TextView
////    private lateinit var pembeliId: String // tambahkan variabel pembeliId untuk menyimpan pembeliId dari intent
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_detail_pesanan)
////
////        // Inisialisasi TextView dan RecyclerView
////        tvStatus = findViewById(R.id.tvStatus)
////        tvTotalPrice = findViewById(R.id.tvTotalPrice)
////        recyclerView = findViewById(R.id.rvCartDetail)
////
////        val orderan = intent.getSerializableExtra("orderan") as? Orderan
////        orderan?.let { order ->
////            val status = order.status
////            val totalHarga = order.totalHarga
////            pembeliId = order.pembeliId // Simpan pembeliId dari intent
////
////            tvStatus.text = status
////            tvTotalPrice.text = "Rp $totalHarga"
////        }
////
////        adapterDetailPesanan = AdapterDetailPesanan()
////        recyclerView.apply {
////            layoutManager = LinearLayoutManager(this@DetailPesanan)
////            adapter = adapterDetailPesanan
////        }
////
////        val btnGetData = findViewById<Button>(R.id.btnGetData)
////        btnGetData.setOnClickListener {
////            getDataFromFirestore() // Panggil fungsi untuk mendapatkan data dari Firestore saat tombol ditekan
////        }
////    }
////
////    private fun getDataFromFirestore() {
////        firestore.collection("Transaksi")
////            .document(pembeliId)
////            .get()
////            .addOnSuccessListener { document ->
////                if (document != null && document.exists()) {
////                    val transaksi = document.toObject(TransaksiDetail::class.java)
////                    transaksi?.let { data ->
////                        val cartItems = data.cartItems
////                        if (cartItems != null) {
////                            adapterDetailPesanan.setData(cartItems)
////                        } else {
////                            Log.d("DetailPesanan", "Cart items not found")
////                        }
////                    }
////                } else {
////                    Log.d("DetailPesanan", "Transaksi document not found")
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.e("DetailPesanan", "Error fetching transaksi data", exception)
////            }
////    }
////}
//
//
//
//
//
//
////class DetailPesanan : AppCompatActivity() {
////
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var adapter: AdapterDetailPesanan
////    private lateinit var firestore: FirebaseFirestore
////    private lateinit var pembeliId: String
////    private lateinit var idTransaksi: String
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_detail_pesanan)
////
////        recyclerView = findViewById(R.id.rvCart)
////        recyclerView.layoutManager = LinearLayoutManager(this)
////        adapter = AdapterDetailPesanan()
////        recyclerView.adapter = adapter
////
////        firestore = FirebaseFirestore.getInstance()
////        pembeliId = intent.getStringExtra("pembeliId") ?: ""
////        idTransaksi = intent.getStringExtra("idTransaksi") ?: ""
////
////        // Muat detail pesanan
////        Log.d("DetailPesanan", "Menerima idTransaksi: $idTransaksi dan pembeliId: $pembeliId dari Pesanan")
////        loadDetailPesanan()
////    }
////
////    private fun loadDetailPesanan() {
////        firestore.collection("Transaksi")
////            .document(idTransaksi)
////            .get()
////            .addOnSuccessListener { document ->
////                if (document.exists()) {
////                    val transaksiData = document.toObject(TransaksiDetail::class.java)
////                    if (transaksiData != null) {
////                        // Memeriksa apakah idTransaksi cocok
////                        if (transaksiData.idTransaksi == idTransaksi) {
////                            // Ambil cartItems dari data transaksi
////                            val cartItems = transaksiData.cartItems?.filter { it.pembeliId == pembeliId }
////                            if (cartItems != null) {
////                                // Jika ingin menampilkan status dan totalHarga, tambahkan ke RecyclerView
////                                val detailPesananItems = mutableListOf<CartItem>()
////                                detailPesananItems.addAll(cartItems)
////                                // Tambahkan status dan totalHarga ke RecyclerView
////                                val statusItem = CartItem("", "", 0.0, 0, "", "", transaksiData.status)
////                                val totalHargaItem = CartItem("", "Total Harga", transaksiData.totalHarga, 0, "", "", "")
////                                detailPesananItems.add(statusItem)
////                                detailPesananItems.add(totalHargaItem)
////                                adapter.setData(detailPesananItems)
////                            }
////                        }
////                    }
////                }
////            }
////    }
////}
//
//
////class DetailPesanan : AppCompatActivity() {
////
////    private lateinit var recyclerView: RecyclerView
////    private lateinit var adapter: AdapterDetailPesanan
////    private lateinit var firestore: FirebaseFirestore
////    private lateinit var pembeliId: String
////    private lateinit var pedagangId: String
////    private lateinit var idTransaksi: String
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_detail_pesanan)
////
////        recyclerView = findViewById(R.id.rvCart)
////        recyclerView.layoutManager = LinearLayoutManager(this)
////        adapter = AdapterDetailPesanan()
////        recyclerView.adapter = adapter
////
////        firestore = FirebaseFirestore.getInstance()
////        pembeliId = intent.getStringExtra("userID") ?: ""
////        idTransaksi = intent.getStringExtra("idTransaksi") ?: ""
////
////        // Dapatkan pedagangId dari Firebase Authentication
////        getPedagangIdAndLoadData()
////    }
////
////    private fun getPedagangIdAndLoadData() {
////        val firebaseAuth = FirebaseAuth.getInstance()
////        val currentUser = firebaseAuth.currentUser
////        if (currentUser != null) {
////            // Pengguna masuk
////            pedagangId = currentUser.uid
////            Log.d("DetailPesanan", "Pedagang ID: $pedagangId")
////            // Sekarang Anda bisa memanggil getTransaksiDetail di sini dengan pedagangId yang didapatkan
////            getTransaksiDetail(idTransaksi, pedagangId)
////        } else {
////            // Pengguna belum masuk atau sesi masuk sudah berakhir
////            Log.d("DetailPesanan", "Tidak ada pengguna yang masuk")
////            // Lakukan penanganan kesalahan atau tindakan yang sesuai di sini
////        }
////    }
////    private fun getTransaksiDetail(idTransaksi: String, pembeliId: String) {
////        firestore.collection("Transaksi")
////            .document(idTransaksi)
////            .get()
////            .addOnSuccessListener { document ->
////                if (document.exists()) {
////                    val transaksiData = document.toObject(TransaksiDetail::class.java)
////                    if (transaksiData != null) {
////                        // Memeriksa apakah idTransaksi cocok
////                        if (transaksiData.idTransaksi == idTransaksi) {
////                            // Ambil cartItems dari data transaksi
////                            val cartItems = transaksiData.cartItems?.filter { it.pembeliId == pembeliId }
////                            if (cartItems != null) {
////                                adapter.setData(cartItems)
////                            }
////                            Log.d("DetailPesanan", "Transaksi berhasil didapatkan: $transaksiData")
////                        } else {
////                            Log.d("DetailPesanan", "idTransaksi tidak cocok: ${transaksiData.idTransaksi} != $idTransaksi")
////                        }
////                    } else {
////                        Log.d("DetailPesanan", "Data transaksi null")
////                    }
////                } else {
////                    Log.d("DetailPesanan", "Tidak ada transaksi dengan idTransaksi: $idTransaksi")
////                    adapter.setData(emptyList())
////                }
////            }
////            .addOnFailureListener { exception ->
////                Log.e("DetailPesanan", "Gagal mendapatkan transaksi", exception)
////                adapter.setData(emptyList())
////            }
////    }
////
////
////
//////    private fun getTransaksiDetail(idTransaksi: String, pedagangId: String) {
//////        firestore.collection("Transaksi")
//////            .document(idTransaksi)
//////            .get()
//////            .addOnSuccessListener { document ->
//////                if (document.exists()) {
//////                    val transaksiData = document.toObject(TransaksiDetail::class.java)
//////                    if (transaksiData != null) {
//////                        // Pastikan idTransaksi di firestore sama dengan yang diterima dari intent
//////                        if (transaksiData.idTransaksi == idTransaksi) {
//////                            // Ambil cartItems dari data transaksi yang memiliki pedagangId yang sesuai dengan pedagang yang sedang login
//////                            val cartItems = transaksiData.cartItems?.filter { it.pedagangId == pedagangId }
//////                            // Tampilkan data cartItems ke dalam RecyclerView
//////                            if (cartItems != null) {
//////                                adapter.setData(cartItems)
//////                            }
//////                            // Tambahkan logcat untuk memeriksa data transaksi
//////                            Log.d("DetailPesanan", "Transaksi berhasil didapatkan: $transaksiData")
//////                        } else {
//////                            Log.d("DetailPesanan", "idTransaksi tidak cocok: ${transaksiData.idTransaksi} != $idTransaksi")
//////                        }
//////                    } else {
//////                        Log.d("DetailPesanan", "Data transaksi null")
//////                    }
//////                } else {
//////                    Log.d("DetailPesanan", "Tidak ada transaksi dengan idTransaksi: $idTransaksi")
//////                    // Tampilkan pesan kesalahan kepada pengguna jika transaksi tidak ditemukan
//////                    adapter.setData(emptyList())
//////                }
//////            }
//////            .addOnFailureListener { exception ->
//////                Log.e("DetailPesanan", "Gagal mendapatkan transaksi", exception)
//////                // Tampilkan pesan kesalahan kepada pengguna jika terjadi kesalahan dalam mengambil data transaksi
//////                adapter.setData(emptyList())
//////            }
//////    }
////}
