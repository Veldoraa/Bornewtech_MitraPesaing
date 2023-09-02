package com.bornewtech.marketplacepesaing.ui.pedagang.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityUpdateBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateBarang : AppCompatActivity() {
    private lateinit var binding : ActivityUpdateBarangBinding
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()

        binding.btnUpdateBarang.setOnClickListener {
            val uName = binding.updNamaProduk.text.toString()
            val uKategori = binding.updKategori.text.toString()
            val uSatuan = binding.updSatuan.text.toString()
            val uHarga = binding.updHargaBarang.text.toString()

            val updateBarang = mapOf(
                "Nama Produk" to uName,
                "Kategori Produk" to uKategori,
                "Satuan Produk" to uSatuan,
                "Harga Produk" to uHarga
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("products").document(userId).update(updateBarang)
            Toast.makeText(this, "Sukses mengupdate barang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("products").document(userId)
        referensi.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("Nama Produk")?.toString()
                val kategori = it.data?.get("Kategori Produk")?.toString()
                val satuan = it.data?.get("Satuan Produk")?.toString()
                val harga = it.data?.get("Harga Produk")?.toString()

                binding.updNamaProduk.setText(name)
                binding.updKategori.setText(kategori)
                binding.updSatuan.setText(satuan)
                binding.updHargaBarang.setText(harga)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Gagal mengupdate barang", Toast.LENGTH_SHORT).show()
            }
    }
}