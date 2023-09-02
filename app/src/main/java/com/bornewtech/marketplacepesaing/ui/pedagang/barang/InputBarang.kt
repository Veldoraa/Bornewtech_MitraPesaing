package com.bornewtech.marketplacepesaing.ui.pedagang.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityInputBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnTambahBarang.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            val name = binding.inpNamaProduk.text.toString().trim()
            val kategori = binding.inpKategori.text.toString().trim()
            val satuan = binding.inpSatuan.text.toString().trim()
            val harga = binding.inpHargaBarang.text.toString().trim()

            val barangMap = hashMapOf(
                "Nama Produk" to name,
                "Kategori Produk" to kategori,
                "Satuan Produk" to satuan,
                "Harga Produk" to harga
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("products").document(userId).set(barangMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProduk.text.clear()
                    binding.inpKategori.text.clear()
                    binding.inpSatuan.text.clear()
                    binding.inpHargaBarang.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}