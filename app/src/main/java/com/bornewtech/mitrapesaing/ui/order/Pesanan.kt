package com.bornewtech.mitrapesaing.ui.order

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Transaction
import com.bornewtech.mitrapesaing.databinding.ActivityPesananBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Pesanan : AppCompatActivity() {
    private lateinit var binding: ActivityPesananBinding
    private lateinit var adapterPesanan: AdapterPesanan
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView dan AdapterPesanan
        adapterPesanan = AdapterPesanan(emptyList())
        binding.rvProductsPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvProductsPesanan.adapter = adapterPesanan

        // Mendapatkan data dari Firestore
        getDataFromFirestore()
    }

    private fun getDataFromFirestore() {
        // Mendapatkan UID pengguna saat ini
        val currentUserUid = auth.currentUser?.uid

        if (currentUserUid != null) {
            // Mendapatkan data transaksi dari Firestore berdasarkan UID pembeli
            firestore.collection("Transaksi")
                .whereEqualTo("cartItems.pembeliId", currentUserUid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val transactions = querySnapshot.documents.map { document ->
                            document.toObject(Transaction::class.java)
                        }.filterNotNull()

                        // Menetapkan data transaksi ke AdapterPesanan
                        adapterPesanan.setTransactions(transactions)
                        Log.d("Pesanan", "Transactions: $transactions")
                    } else {
                        // Dokumen tidak ditemukan
                        Log.d("Pesanan", "No transaction document found for UID: $currentUserUid")
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mengambil data dari Firestore
                    Log.e("Pesanan", "Failed to fetch data from Firestore: $exception")
                }
        } else {
            Log.d("Pesanan", "Current user is not authenticated.")
            // Handle the case where the user is not authenticated
        }
    }
}
