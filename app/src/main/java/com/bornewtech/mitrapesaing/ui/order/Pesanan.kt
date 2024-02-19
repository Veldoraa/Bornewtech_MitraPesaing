package com.bornewtech.mitrapesaing.ui.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.google.firebase.firestore.FirebaseFirestore

class Pesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterPesanan
    private lateinit var dataList: MutableList<Orderan>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)

        recyclerView = findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dataList = mutableListOf()
        adapter = AdapterPesanan(dataList)
        recyclerView.adapter = adapter

        firestore = FirebaseFirestore.getInstance()
        currentUserID = intent.getStringExtra("userID") ?: ""

        // Mendapatkan data dari Firestore
        getFirestoreData()

        // Set listener untuk item RecyclerView
        adapter.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
            override fun onItemClick(orderan: Orderan) {
                // Mengarahkan ke DetailPesananActivity dengan membawa data Orderan yang diklik
                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
                intent.putExtra("detailPesanan", orderan)
                intent.putExtra("userID", currentUserID)
                startActivity(intent)
            }
        })
    }

    private fun getFirestoreData() {
        firestore.collection("riwayatTransaksi")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Mendapatkan UID dokumen
                    val userId = document.id
                    // Cetak UID ke logcat
                    Log.d("Pesanan", "UID Dokumen: $userId")

                    val data = document.toObject(Orderan::class.java)
                    dataList.add(data)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Menangani error
            }
    }
}
