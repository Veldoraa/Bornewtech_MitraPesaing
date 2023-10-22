package com.bornewtech.mitrapesaing.ui.barang

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bornewtech.mitrapesaing.data.camera.utility.getImageUri
import com.bornewtech.mitrapesaing.databinding.ActivityInputBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    private var currentImageUri: Uri? = null
    private var dbBarang = Firebase.firestore
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        storageRef = FirebaseStorage.getInstance()


        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }

        binding.btnTambahBarang.setOnClickListener {
            val name = binding.inpNamaProduk.text.toString().trim()
            val kategori = binding.inpKategori.text.toString().trim()
            val satuan = binding.inpSatuan.text.toString().trim()
            val stok = binding.inpStok.text.toString().trim()
            val harga = binding.inpHarga.text.toString().trim()

            val barangMap = hashMapOf(
                "Nama Produk" to name,
                "Kategori Produk" to kategori,
                "Satuan Produk" to satuan,
                "Stok Produk" to stok,
                "Harga Produk" to harga
            )

            //Nambah Barang
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseProduk = dbBarang.collection("Products").document(userId)
            databaseProduk.update("List Produk Dari User Id Ini", FieldValue.arrayUnion(barangMap))
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProduk.text.clear()
                    binding.inpKategori.text.clear()
                    binding.inpSatuan.text.clear()
                    binding.inpStok.text.clear()
                    binding.inpHarga.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data", Toast.LENGTH_SHORT).show()
                }

            // Nambah Gambar Barang
            currentImageUri?.let { uri ->
                storageRef.getReference("Gambar Barang")
                    .child(System.currentTimeMillis().toString())
                    .putFile(uri)
                    .addOnSuccessListener {
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        val mapImage = mapOf(
                            "url" to it.toString()
                        )
                        val databaseReferences =
                            FirebaseDatabase.getInstance().getReference("gambarBarang")
                        databaseReferences.child(userId).setValue(mapImage)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }

            }
        }
    }

    // Upload gambar dengan galeri
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    // upload gambar dengan Kamera
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    // Menampilkan Gambar di Tampilan kotak image
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivgambarBarang.setImageURI(it)
        }
    }

}