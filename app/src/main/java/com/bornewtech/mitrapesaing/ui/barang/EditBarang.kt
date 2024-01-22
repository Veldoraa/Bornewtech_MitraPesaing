package com.bornewtech.mitrapesaing.ui.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.ProductItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityEditBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditBarang : AppCompatActivity() {
    private lateinit var binding: ActivityEditBarangBinding
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setData()

        binding.btnUpdateBarang.setOnClickListener {
            val uNama = binding.updNamaProduk.text.toString().trim()
            val uKategori = binding.updKategori.text.toString().trim()
            val uSatuan = binding.updSatuan.text.toString().trim()
            val uStok = binding.updStokBarang.text.toString().trim()
            val uHarga = binding.updHargaBarang.text.toString().trim()

            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val referensi = dbBarang.collection("Products").document(userId)

            // Dapatkan produkId dari Intent
            val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem
            val produkId = selectedItem?.produkId

            referensi.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val array = document.get("productList") as ArrayList<HashMap<String, Any>>
                        if (array.isNotEmpty()) {
                            for (i in array.indices) {
                                val produkMap = array[i]
                                val existingProdukId = produkMap["produkId"] as String
                                if (existingProdukId == produkId) {
                                    // Update hanya field-field tertentu
                                    array[i] = hashMapOf(
                                        "produkId" to existingProdukId,
                                        "produkNama" to uNama,
                                        "produkKategori" to uKategori,
                                        "produkSatuan" to uSatuan,
                                        "produkStok" to uStok,
                                        "produkHarga" to uHarga,
                                        "imageUrl" to produkMap["imageUrl"] as String // Cast imageUrl to String
                                    )
                                    break // Keluar dari loop setelah menemukan produk yang sesuai
                                }
                            }

                            // Update ke Firestore
                            referensi.update("productList", array)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                                    finish() // Selesaikan aktivitas setelah update berhasil
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                                }

                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        referensi.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val products = document.toObject(Products::class.java)
                    val productList = products?.productList

                    if (!productList.isNullOrEmpty()) {
                        val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem

                        if (selectedItem != null) {
                            // Use selectedItem data to populate the UI
                            binding.updNamaProduk.setText(selectedItem.produkNama)
                            binding.updKategori.setText(selectedItem.produkKategori)
                            binding.updSatuan.setText(selectedItem.produkSatuan)
                            binding.updStokBarang.setText(selectedItem.produkStok)
                            binding.updHargaBarang.setText(selectedItem.produkHarga)
                        } else {
                            // Handle the case when no item is selected (optional)
                            Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengupdate barang", Toast.LENGTH_SHORT).show()
            }
    }
}