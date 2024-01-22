package com.bornewtech.mitrapesaing.ui.barang

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bornewtech.mitrapesaing.data.camera.utility.getImageUri
import com.bornewtech.mitrapesaing.databinding.ActivityInputBarangBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    private var currentImageUri: Uri? = null
    private var dbBarang = Firebase.firestore
    private var storageRef = Firebase.storage
    private var pedagangId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        storageRef = FirebaseStorage.getInstance()

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }

        // Mendapatkan ID pengguna yang sedang login
        pedagangId = FirebaseAuth.getInstance().currentUser?.uid

        binding.btnTambahBarang.setOnClickListener { saveData() }
    }
    // Fungsi untuk menghasilkan ID random
    private fun generateRandomId(): String {
        // Logic untuk menghasilkan ID random, Anda bisa sesuaikan sesuai kebutuhan
        return java.util.UUID.randomUUID().toString()
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
            // Simpan data gambar ke Firebase Storage dan Firestore
//            saveImageData()
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
            // Simpan data gambar ke Firebase Storage dan Firestore
//            saveImageData()
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

    //save data
    //save data
    private fun saveData() {
        val nama = binding.inpNamaProduk.text.toString().trim()
        val kategori = binding.inpKategori.text.toString().trim()
        val satuan = binding.inpSatuan.text.toString().trim()
        val stok = binding.inpStok.text.toString().trim()
        val harga = binding.inpHarga.text.toString().trim()

        // Check if user is logged in
        if (pedagangId != null) {
            currentImageUri?.let { uri ->
                uploadImage(uri, pedagangId!!, nama, kategori, satuan, stok, harga)
            } ?: run {
                Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(uri: Uri, pedagangId: String, nama: String, kategori: String, satuan: String, stok: String, harga: String) {
        val storageReference = storageRef.getReference("Gambar Barang")
            .child(System.currentTimeMillis().toString())

        // Kompresi gambar sebelum mengunggah
        val compressedUri = compressImage(uri)
        if (compressedUri != null) {
            storageReference.putFile(compressedUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Dapatkan URL gambar setelah berhasil di-upload
                    storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()

                        // Memasukkan imageUrl ke dalam barangMap
                        val barangMap = hashMapOf(
                            "pedagangId" to pedagangId,
                            "produkId" to generateRandomId(),
                            "produkNama" to nama,
                            "produkKategori" to kategori,
                            "produkSatuan" to satuan,
                            "produkStok" to stok,
                            "produkHarga" to harga,
                            "imageUrl" to imageUrl
                        )

                        // Nambah Barang
                        val databaseProduk = dbBarang.collection("Products").document(pedagangId)

                        databaseProduk.update("productList", FieldValue.arrayUnion(barangMap))
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
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Gagal mengompres gambar", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengompres gambar
    private fun compressImage(uri: Uri): Uri? {
        return try {
            val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val rotation = getRotationFromGallery(uri) // Dapatkan rotasi gambar dari galeri
            val orientedBitmap = rotateBitmap(originalBitmap, rotation)

            val outputStream = ByteArrayOutputStream()
            orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val compressedByteArray = outputStream.toByteArray()
            val compressedBitmap = BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)

            // Menyimpan gambar yang terkompres ke file temporer
            val tempFile = createTempFile("tempImage", ".jpg")
            val tempUri = Uri.fromFile(tempFile)
            val tempOutputStream = FileOutputStream(tempFile)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, tempOutputStream)
            tempOutputStream.close()

            tempUri
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk mendapatkan rotasi gambar dari galeri
    @SuppressLint("Range")
    private fun getRotationFromGallery(uri: Uri): Int {
        val columns = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cursor = contentResolver.query(uri, columns, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val orientation = it.getInt(it.getColumnIndex(columns[0]))
                if (orientation != -1) {
                    return orientation
                }
            }
        }
        return 0
    }

    // Fungsi untuk memutar bitmap sesuai dengan rotasi
    private fun rotateBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}