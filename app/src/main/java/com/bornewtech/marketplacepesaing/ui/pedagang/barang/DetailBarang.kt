package com.bornewtech.marketplacepesaing.ui.pedagang.barang

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityDetailBarangBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailBarang : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBarangBinding
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("products").document(userId)
        //get Data ke detail barang
        referensi.get().addOnSuccessListener {
            if (it != null) {
                val barangNama = it.data?.get("Nama Produk").toString()
                val barangKategori = it.data?.get("Kategori Produk").toString()
                val barangSatuan = it.data?.get("Satuan Produk").toString()
                val barangHarga = it.data?.get("Harga Produk").toString()

                binding.namaBarang.text = barangNama
                binding.hargaBarang.text = barangHarga
                binding.category.text = barangKategori
                binding.satuan.text = barangSatuan
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Menarik Data", Toast.LENGTH_SHORT).show()
            }
        binding.btnUpdateBarang.setOnClickListener{
            startActivity(Intent(this, UpdateBarang::class.java))
            finish()
        }

        binding.btnDltBarang.setOnClickListener{
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("products").document(userId)
                .delete()
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
        }
    }
}