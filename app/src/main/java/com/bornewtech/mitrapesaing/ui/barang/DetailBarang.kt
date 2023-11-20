package com.bornewtech.mitrapesaing.ui.barang

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityDetailBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private var dbBarang = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val nama = binding.namaBarang.text.toString().trim()
        val kategori = binding.category.text.toString().trim()
        val satuan = binding.satuan.text.toString().trim()
        val stok = binding.stokBarang.text.toString().trim()
        val harga = binding.hargaBarang.text.toString().trim()

        val barangMap = hashMapOf(
            "produkNama" to nama,
            "produkKategori" to kategori,
            "produkSatuan" to satuan,
            "produkStok" to stok,
            "produkHarga" to harga
        )

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        //get Data ke detail barang
        referensi.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val array = document.get("productList") as ArrayList<*>
                    if (array.isNotEmpty()){
                        array[0]
                    }
                }
//            if (it != null) {
//
//                val barangNama = it.data?.get("Nama Produk").toString()
//                val barangKategori = it.data?.get("Kategori Produk").toString()
//                val barangSatuan = it.data?.get("Satuan Produk").toString()
//                val barangStok = it.data?.get("Stok Produk").toString()
//                val barangHarga = it.data?.get("Harga Produk").toString()
//
//                binding.namaBarang.text = barangNama
//                binding.category.text = barangKategori
//                binding.satuan.text = barangSatuan
//                binding.stokBarang.text = barangStok
//                binding.hargaBarang.text = barangHarga
//            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Menarik Data", Toast.LENGTH_SHORT).show()
            }
        binding.btnUpdateBarang.setOnClickListener{
            startActivity(Intent(this, EditBarang::class.java))
            finish()
        }

        binding.btnDltBarang.setOnClickListener{
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("Products").document(userId)
                .delete()
                .addOnSuccessListener {
                    val intent = Intent(this, DetailBarang::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
        }
    }
}