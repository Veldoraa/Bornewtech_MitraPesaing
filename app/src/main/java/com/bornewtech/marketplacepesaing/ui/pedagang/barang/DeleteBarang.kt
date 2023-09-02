package com.bornewtech.marketplacepesaing.ui.pedagang.barang

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityDeleteBarangBinding
import com.bornewtech.marketplacepesaing.databinding.ActivityDetailBarangBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeleteBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBarangBinding
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        binding.btnDeleteBarang.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("products").document(userId)
                .delete()
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("products").document(userId)
        referensi.get().addOnSuccessListener {
            if (it != null) {
                val nama = it.data?.get("Nama Produk")?.toString()
                val kategori = it.data?.get("Kategori Produk")?.toString()
                val satuan = it.data?.get("Satuan Produk")?.toString()
                val harga = it.data?.get("Harga Produk")?.toString()

                binding.dltNamaProduk.setText(nama)
                binding.dltKategori.setText(kategori)
                binding.dltSatuan.setText(satuan)
                binding.dltHargabrng.setText(harga)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Gagal menghapus barang", Toast.LENGTH_SHORT).show()
            }
    }
}