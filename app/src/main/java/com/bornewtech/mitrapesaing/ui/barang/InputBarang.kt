package com.bornewtech.mitrapesaing.ui.barang

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
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
import java.io.IOException
import java.util.Locale


class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    private var currentImageUri: Uri? = null
    private var dbBarang = Firebase.firestore
    private var storageRef = Firebase.storage
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        storageRef = FirebaseStorage.getInstance()

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // share location
//        binding.cbShareLoc.setOnClickListener(){
//            // check self permission
//            checkLocationPermission()
//        }

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
//            val userId = FirebaseAuth.getInstance().currentUser!!.uid
//            dbBarang.collection("Products").document(userId).set(barangMap)
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseProduk = dbBarang.collection("Products").document(userId)
            databaseProduk.update("listProduct", FieldValue.arrayUnion(barangMap))
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

//    private fun checkLocationPermission() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            // when permission already grant
//            checkGPS()
//        } else {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
//        }
//    }
//
//    private fun checkGPS() {
//        locationRequest = LocationRequest.create()
//        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval = 5000
//        locationRequest.fastestInterval = 2000
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//        builder.setAlwaysShow(true)
//
//        val result = LocationServices.getSettingsClient(
//            this.applicationContext
//        )
//            .checkLocationSettings(builder.build())
//        result.addOnCompleteListener { task ->
//            try {
//                // ketika GPS on
//                val respone = task.getResult(
//                    ApiException::class.java
//                )
//                getUserLocation()
//            } catch (e: ApiException){
//                // ketika GPS off
//                e.printStackTrace()
//                when (e.statusCode){
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//                        // kita mengirim request untuk enable gps
//                        val resolvableApiException = e as ResolvableApiException
//                        resolvableApiException.startResolutionForResult(this, 1001)
//                    } catch (sendIntentException : IntentSender.SendIntentException) {
//                    }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                        //ketika setting unavailable
//                    }
//                }
//            }
//        }
//    }
//
//    private fun getUserLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
//            val location = task.getResult()
//            if (location != null){
//                try {
//                    val geocoder = Geocoder(this, Locale.getDefault())
//                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                    // set alamat di dalam textview loc_text
//                    val address_line = address?.get(0)?.getAddressLine(0)
//                    binding.locText.setText(address_line)
//                    val address_location = address?.get(0)?.getAddressLine(0)
//
//                    openLocation(address_location.toString())
//                } catch (e: IOException){
//
//                }
//            }
//        }
//    }
//
//    private fun openLocation(location: String) {
//        // open lokasi di google map
//        // set button klik
//        binding.locText.setOnClickListener() {
//            if (!binding.locText.text.isEmpty()){
//                // ketika lokasi tidak kosong
//                val uri = Uri.parse("geo:0, 0?q=$location")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                intent.setPackage("com.google.android.apps.maps")
//                startActivity(intent)
//            }
//        }
//    }

    // Upload gambar dengan galeri
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    // ngelaunch galeri
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
    // ngelaunch camera
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