package com.bornewtech.mitrapesaing.ui.barang

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.camera.utility.getImageUri
import com.bornewtech.mitrapesaing.databinding.ActivityInputBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    private var currentImageUri: Uri? = null
    private var dbBarang = Firebase.firestore
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        initVars()
        registerClickEvents()

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }

        binding.btnTambahBarang.setOnClickListener {
            val name = binding.inpNamaProduk.text.toString().trim()
            val kategori = binding.inpKategori.text.toString().trim()
            val satuan = binding.inpSatuan.text.toString().trim()
            val harga = binding.inpHarga.text.toString().trim()

            val barangMap = hashMapOf(
                "Nama Produk" to name,
                "Kategori Produk" to kategori,
                "Satuan Produk" to satuan,
                "Harga Produk" to harga
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseProduk = dbBarang.collection("Products").document(userId)
            databaseProduk.update("List Produk Dari User Id Ini", FieldValue.arrayUnion(barangMap))
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProduk.text.clear()
                    binding.inpKategori.text.clear()
                    binding.inpSatuan.text.clear()
                    binding.inpHarga.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data", Toast.LENGTH_SHORT).show()
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

    private fun registerClickEvents(){
        binding.btnTambahBarang.setOnClickListener {
            uploadImage()
        }
        binding.ivgambarBarang.setOnClickListener {
            resultLauncher.launch("Gambar/*")
        }
    }
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()) {
        currentImageUri = it
        binding.ivgambarBarang.setImageURI(it)
    }
    private fun initVars(){
        storageRef = FirebaseStorage.getInstance().reference.child("Gambar")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    private fun uploadImage() {
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        currentImageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["Pictures"] = uri.toString()

                        firebaseFirestore.collection("Gambar Produk").add(map).addOnCompleteListener {
                            firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                Toast.makeText(this, "Berhasil Mengunggah Gambar", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                            binding.ivgambarBarang.setImageResource(R.drawable.vector_ek1)
                        }
                    }

                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    binding.ivgambarBarang.setImageResource(R.drawable.vector_ek1)
                }
            }
        }
    }
}