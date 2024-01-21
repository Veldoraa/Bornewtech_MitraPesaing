package com.bornewtech.mitrapesaing.ui.barang

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.ProductItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityDetailBarangBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private var dbBarang = Firebase.firestore
    private lateinit var gambarBarangDetail: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inisialisasi ImageView
        gambarBarangDetail = findViewById(R.id.img_barang)

        val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem
        if (selectedItem != null) {
            // Use selectedItem data to populate the detail view
            binding.namaBarang.text = selectedItem.produkNama.toString()
            binding.category.text = selectedItem.produkKategori.toString()
            binding.satuan.text = selectedItem.produkSatuan.toString()
            binding.stokBarang.text = selectedItem.produkStok.toString()
            binding.hargaBarang.text = selectedItem.produkHarga.toString()

            // Load image using Glide
            Glide.with(this)
                .load(selectedItem.imageUrl)
                .placeholder(R.drawable.image_baseline)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(gambarBarangDetail)

            // Dapatkan produkId dari Firestore berdasarkan produkNama
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val referensi = dbBarang.collection("Products").document(userId)
            referensi.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val array = document.get("productList") as ArrayList<*>
                        if (array.isNotEmpty()) {
                            for (i in array.indices) {
                                val produkMap = array[i] as Map<*, *>
                                val produkNama = produkMap["produkNama"] as String
                                if (produkNama == selectedItem.produkNama) {
                                    val produkId = produkMap["produkId"] as String
                                    Log.d("DetailBarang", "produkId: $produkId")
                                    break // Keluar dari loop setelah menemukan produk yang sesuai
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Menarik Data", Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnUpdateBarang.setOnClickListener {
            val intent = Intent(this, EditBarang::class.java)
            intent.putExtra("selectedItem", selectedItem)
            startActivity(intent)
            finish()
        }

        binding.btnDltBarang.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("Products").document(userId)
                .delete()
                .addOnSuccessListener {
                    val intent = Intent(this, DetailBarang::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error deleting document", e)
                }
        }
    }
}