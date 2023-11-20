package com.bornewtech.mitrapesaing.ui.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityEditBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

            val updateBarang = hashMapOf(
                "produkNama" to uNama,
                "produkKategori" to uKategori,
                "produkSatuan" to uSatuan,
                "produkStok" to uStok,
                "produkHarga" to uHarga
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val produkUpdate = dbBarang.collection("Products").document(userId)
//            dbBarang.collection("Products").document(userId).update(updateBarang)
            produkUpdate.update("productList", FieldValue.arrayUnion(updateBarang))
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                    binding.updNamaProduk.text.toString()
                    binding.updKategori.text.toString()
                    binding.updSatuan.text.toString()
                    binding.updStokBarang.text.toString()
                    binding.updHargaBarang.text.toString()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                }
//            Toast.makeText(this, "Sukses mengupdate barang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        referensi.get()
            .addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("Nama Produk")?.toString()
                val kategori = it.data?.get("Kategori Produk")?.toString()
                val satuan = it.data?.get("Satuan Produk")?.toString()
                val stok = it.data?.get("Stok Produk")?.toString()
                val harga = it.data?.get("Harga Produk")?.toString()

                binding.updNamaProduk.setText(name)
                binding.updKategori.setText(kategori)
                binding.updSatuan.setText(satuan)
                binding.updStokBarang.setText(stok)
                binding.updHargaBarang.setText(harga)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Gagal mengupdate barang", Toast.LENGTH_SHORT).show()
            }
    }
}