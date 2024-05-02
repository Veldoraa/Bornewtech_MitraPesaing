//package com.bornewtech.mitrapesaing.ui.order
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
//import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Pesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterPesanan: AdapterPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private val pesananList = mutableListOf<Orderan>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pesanan)
//        supportActionBar?.hide()
//
//        recyclerView = findViewById(R.id.rvCart)
//        adapterPesanan = AdapterPesanan(pesananList)
//
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@Pesanan)
//            adapter = adapterPesanan
//        }
//
//        // Set listener untuk item click pada RecyclerView
//        adapterPesanan.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
//            override fun onItemClick(orderan: Orderan) {
//                Log.d("Pesanan", "Item clicked: $orderan")
//
//                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
//                intent.putExtra("pembeliId", orderan.pembeliId)
//                intent.putExtra("status", orderan.status)
//                intent.putExtra("totalHarga", orderan.totalHarga) // Tambahkan totalHarga
//                intent.putExtra("alamatLengkap", orderan.alamatLengkap)
//                intent.putExtra("idTransaksi", orderan.idTransaksi) // Tambahkan idTransaksi
//                startActivity(intent)
//            }
//        })
//
//        loadData()
//    }
//
//    private fun loadData() {
//        val pedagangId = FirebaseAuth.getInstance().currentUser?.uid
//        if (pedagangId != null) {
//            firestore.collection("riwayatTransaksi")
//                .whereEqualTo("pedagangId", pedagangId) // Filter data berdasarkan pedagangId
//                .get()
//                .addOnSuccessListener { result ->
//                    pesananList.clear() // Bersihkan list sebelum menambahkan data baru
//                    for (document in result) {
//                        val orderan = document.toObject(Orderan::class.java)
//                        pesananList.add(orderan)
//                    }
//                    adapterPesanan.notifyDataSetChanged() // Notifikasi perubahan data
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("Pesanan", "Error fetching data", exception)
//                }
//        } else {
//            Log.d("Pesanan", "PedagangId is null")
//        }
//    }
//}





package com.bornewtech.mitrapesaing.ui.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Pesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPesanan: AdapterPesanan
    private val firestore = FirebaseFirestore.getInstance()
    private val pesananList = mutableListOf<Orderan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.rvCart)
        adapterPesanan = AdapterPesanan(pesananList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Pesanan)
            adapter = adapterPesanan
        }

        adapterPesanan.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
            override fun onItemClick(orderan: Orderan) {
                Log.d("Pesanan", "Item clicked: $orderan")

                // Start DetailPesanan activity with selected item
                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
                intent.putExtra("pembeliId", orderan.pembeliId) // Mengirim pembeliId ke DetailPesanan
                intent.putExtra("status", orderan.status) // Mengirim status ke DetailPesanan
                intent.putExtra("totalHarga", orderan.totalHarga) // Mengirim total harga ke DetailPesanan
                intent.putExtra("idTransaksi", orderan.idTransaksi )
                intent.putExtra("alamatLengkap", orderan.alamatLengkap) // Mengirim alamat lengkap ke DetailPesanan
                startActivity(intent)
            }
        })

        loadData()
    }

    private fun loadData() {
        val pedagangId = FirebaseAuth.getInstance().currentUser?.uid
        if (pedagangId != null) {
            firestore.collection("riwayatTransaksi")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val orderan = document.toObject(Orderan::class.java)
                        pesananList.add(orderan)
                    }
                    adapterPesanan.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e("Pesanan", "Error fetching data", exception)
                }
        } else {
            Log.d("Pesanan", "PedagangId is null")
        }
    }
}



//package com.bornewtech.mitrapesaing.ui.order
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
//import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Pesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterPesanan: AdapterPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private val pesananList = mutableListOf<Orderan>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pesanan)
//        supportActionBar?.hide()
//
//        recyclerView = findViewById(R.id.rvCart)
//        adapterPesanan = AdapterPesanan(pesananList)
//
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@Pesanan)
//            adapter = adapterPesanan
//        }
//
//        adapterPesanan.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
//            override fun onItemClick(orderan: Orderan) {
//                Log.d("Pesanan", "Item clicked: $orderan")
//
//                // Start DetailPesanan activity with selected item
//                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
//                intent.putExtra("pembeliId", orderan.pembeliId) // Mengirim pembeliId ke DetailPesanan
//                intent.putExtra("status", orderan.status) // Mengirim status ke DetailPesanan
//                intent.putExtra("totalHarga", orderan.totalHarga) // Mengirim total harga ke DetailPesanan
//                Log.d("Pesanan", "Data sent: pembeliId=${orderan.pembeliId}, status=${orderan.status}, totalHarga=${orderan.totalHarga}") // Logging data yang dikirim
//                startActivity(intent)
//            }
//        })
//
//        loadData()
//    }
//
//    private fun loadData() {
//        val pedagangId = FirebaseAuth.getInstance().currentUser?.uid
//        if (pedagangId != null) {
//            firestore.collection("riwayatTransaksi")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        val orderan = document.toObject(Orderan::class.java)
//                        pesananList.add(orderan)
//                    }
//                    adapterPesanan.notifyDataSetChanged()
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("Pesanan", "Error fetching data", exception)
//                }
//        } else {
//            Log.d("Pesanan", "PedagangId is null")
//        }
//    }
//}



//class Pesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapterPesanan: AdapterPesanan
//    private val firestore = FirebaseFirestore.getInstance()
//    private val pesananList = mutableListOf<Orderan>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pesanan)
//
//        recyclerView = findViewById(R.id.rvCart)
//        adapterPesanan = AdapterPesanan(pesananList)
//
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(this@Pesanan)
//            adapter = adapterPesanan
//        }
//
//        adapterPesanan.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
//            override fun onItemClick(orderan: Orderan) {
//                Log.d("Pesanan", "Item clicked: $orderan, pembeliId: ${orderan.pembeliId}") // Log data pembeliId
//
//                // Mengambil status dan total harga dari item orderan yang diklik
//                val status = orderan.status
//                val totalHarga = orderan.totalHarga
//
//                // Log data status dan totalHarga yang dikirim
//                Log.d("Pesanan", "Sending status: $status")
//                Log.d("Pesanan", "Sending totalHarga: $totalHarga")
//
//                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
//                intent.putExtra("orderan", orderan) // Mengirim seluruh objek orderan ke DetailPesanan
//                startActivity(intent)
//            }
//        })
//
//
//        loadData()
//    }
//
//    private fun loadData() {
//        val pedagangId = FirebaseAuth.getInstance().currentUser?.uid
//        if (pedagangId != null) {
//            firestore.collection("riwayatTransaksi")
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        val orderan = document.toObject(Orderan::class.java)
//                        pesananList.add(orderan)
//                    }
//                    adapterPesanan.notifyDataSetChanged()
//                }
//                .addOnFailureListener { exception ->
//                    Log.e("Pesanan", "Error fetching data", exception)
//                }
//        } else {
//            Log.d("Pesanan", "PedagangId is null")
//        }
//    }
//}







//class Pesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AdapterPesanan
//    private lateinit var dataList: MutableList<Orderan>
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var currentUserID: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pesanan)
//
//        recyclerView = findViewById(R.id.rvCart)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        dataList = mutableListOf()
//        adapter = AdapterPesanan(dataList)
//        recyclerView.adapter = adapter
//
//        firestore = FirebaseFirestore.getInstance()
//        currentUserID = intent.getStringExtra("userID") ?: ""
//
//        // Mendapatkan data dari Firestore
//        getFirestoreData()
//
//        // Set listener untuk item RecyclerView
//        adapter.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
//            override fun onItemClick(orderan: Orderan) {
//                // Mengarahkan ke DetailPesananActivity dengan membawa data Orderan yang diklik
//                Log.d("Pesanan", "Mengirim idTransaksi: ${orderan.idTransaksi} dan pembeliId: ${orderan.pembeliId} ke DetailPesanan")
//                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
//                intent.putExtra("idTransaksi", orderan.idTransaksi) // Mengirim idTransaksi
//                intent.putExtra("pembeliId", orderan.pembeliId) // Mengirim pembeliId
//                startActivity(intent)
//            }
//        })
//    }
//
//
//    private fun getFirestoreData() {
//        firestore.collection("riwayatTransaksi")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val data = document.toObject(Orderan::class.java)
//                    dataList.add(data)
//                }
//                // Setelah mendapatkan semua data, perbarui RecyclerView
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { exception ->
//                // Menangani error
//            }
//    }
//}

//class Pesanan : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AdapterPesanan
//    private lateinit var dataList: MutableList<Orderan>
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var currentUserID: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pesanan)
//
//        recyclerView = findViewById(R.id.rvCart)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        dataList = mutableListOf()
//        adapter = AdapterPesanan(dataList)
//        recyclerView.adapter = adapter
//
//        firestore = FirebaseFirestore.getInstance()
//        currentUserID = intent.getStringExtra("userID") ?: ""
//
//        // Mendapatkan data dari Firestore
//        getFirestoreData()
//
//        // Set listener untuk item RecyclerView
//        adapter.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
//            override fun onItemClick(orderan: Orderan) {
//                // Mengarahkan ke DetailPesananActivity dengan membawa data Orderan yang diklik
//                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
//                intent.putExtra("detailPesanan", orderan)
//                intent.putExtra("idTransaksi", orderan.idTransaksi) // Mengirim idTransaksi
//                intent.putExtra("userID", orderan.pembeliId)
//                Log.d("Pesanan", "Mengirim userID: ${orderan.pembeliId} dan idTransaksi: ${orderan.idTransaksi} ke DetailPesanan")
//                startActivity(intent)
//            }
//        })
//    }
////    private fun getFirestoreData() {
////        firestore.collection("riwayatTransaksi")
////            .whereEqualTo("pedagangId", currentUserID) // Filter berdasarkan pedagang yang login
////            .get()
////            .addOnSuccessListener { documents ->
////                for (document in documents) {
////                    val data = document.toObject(Orderan::class.java)
////                    dataList.add(data)
////                }
////                adapter.notifyDataSetChanged()
////            }
////            .addOnFailureListener { exception ->
////                Log.e("Pesanan", "Error getting documents: ", exception)
////            }
////    }
//
//    private fun getFirestoreData() {
//        firestore.collection("riwayatTransaksi")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    // Mendapatkan UID dokumen
//                    val userId = document.id
//                    // Cetak UID ke logcat
//                    Log.d("Pesanan", "UID Dokumen: $userId")
//
//                    val data = document.toObject(Orderan::class.java)
//                    dataList.add(data)
//                }
//                // Setelah mendapatkan semua data, perbarui RecyclerView
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { exception ->
//                // Menangani error
//            }
//    }
//}