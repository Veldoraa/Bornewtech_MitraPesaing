package com.bornewtech.mitrapesaing.ui.barang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.ProductItem
import com.bornewtech.mitrapesaing.databinding.ActivityDetailBarangBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

                                    // Add click listener for the delete button
                                    binding.btnDltBarang.setOnClickListener {
                                        // Call the function to delete the product
                                        deleteProduct(userId, produkId)
                                    }

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
    }

    // Function to delete the product
    private fun deleteProduct(userId: String, productId: String) {
        val productRef = dbBarang.collection("Products").document(userId)

        // Remove the product from the productList array
        productRef.update("productList", FieldValue.arrayRemove(mapOf("produkId" to productId)))
            .addOnSuccessListener {
                // Delete the document associated with the product from UserProducts collection
                dbBarang.collection("Products").document(userId).collection("UserProducts")
                    .document(productId)
                    .delete()
                    .addOnSuccessListener {
                        // Delete the document associated with the product from Products collection
                        dbBarang.collection("Products").document(productId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                                finish() // Finish the activity after deleting the product
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Gagal menghapus produk dari Products", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menghapus produk dari UserProducts", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus produk dari productList", Toast.LENGTH_SHORT).show()
            }
    }
}
