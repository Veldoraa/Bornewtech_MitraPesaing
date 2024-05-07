package com.bornewtech.mitrapesaing.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bornewtech.mitrapesaing.databinding.ActivityEditProfilBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.util.Locale

class EditProfil : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding
    private val dbProfil = Firebase.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val UPDATE_INTERVAL = 300000L // 5 menit dalam milidetik
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission() // Memastikan izin lokasi diberikan

        binding.backToProfil.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }

        setData()

        binding.btnSimpanProfil.setOnClickListener {
            saveProfileData()
        }

        startCountdownTimer() // Mulai hitung mundur untuk pembaruan lokasi otomatis
    }

    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val refProfil = dbProfil.collection("Pedagang").document(userId)

            refProfil.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val nama = documentSnapshot.getString("namaLengkap")
                        val noHp = documentSnapshot.getString("noHpAktif")
                        val email = documentSnapshot.getString("email")
                        val alamat = documentSnapshot.getString("alamatLengkap")

                        // Mengatur data yang didapat dari Firestore ke komponen UI
                        binding.inpNamaProfil.setText(nama)
                        binding.inpNoHpProfil.setText(noHp)
                        binding.inpEmailProfil.setText(email)
                        binding.inpAlamatProfil.setText(alamat)
                    } else {
                        Toast.makeText(this, "Data profil tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User tidak terdeteksi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    updateLocationData(location.latitude, location.longitude)
                }
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.firstOrNull()?.let { location ->
                        updateLocationData(location.latitude, location.longitude)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(UPDATE_INTERVAL, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.tvCountdownTimer.text = "Diperbarui dalam $secondsLeft detik"
            }

            override fun onFinish() {
                startLocationUpdates() // Mulai pembaruan lokasi
                startCountdownTimer() // Mulai ulang hitung mundur
            }
        }
        countdownTimer?.start()
    }

    private fun updateLocationData(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val addressLine = addressList[0]?.getAddressLine(0)
                    binding.inpCurrentLocProfil.setText(addressLine)
                    saveLocationToFirebase(latitude, longitude) // Simpan lokasi ke Firebase
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )

            databaseReference.child("userLocations").child("pedagang").child(userId).setValue(locationData)
                .addOnSuccessListener {
                    // Lokasi berhasil disimpan
                }
                .addOnFailureListener {
                    // Gagal menyimpan lokasi
                }
        }
    }

    private fun saveProfileData() {
        val nameProfil = binding.inpNamaProfil.text.toString().trim()
        val noHpProfil = binding.inpNoHpProfil.text.toString().trim()
        val emailProfil = binding.inpEmailProfil.text.toString().trim()
        val alamatProfil = binding.inpAlamatProfil.text.toString().trim()

        val profilMap = hashMapOf(
            "namaLengkap" to nameProfil,
            "noHpAktif" to noHpProfil,
            "email" to emailProfil,
            "alamatLengkap" to alamatProfil
        )

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        dbProfil.collection("Pedagang").document(userId).set(profilMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan Data Profil", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer?.cancel() // Batalkan timer saat aktivitas dihentikan
    }
}


//package com.bornewtech.mitrapesaing.ui.profile
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.IntentSender
//import android.content.pm.PackageManager
//import android.location.Geocoder
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import com.bornewtech.mitrapesaing.databinding.ActivityEditProfilBinding
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.common.api.ResolvableApiException
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.LocationSettingsRequest
//import com.google.android.gms.location.LocationSettingsStatusCodes
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
//import java.io.IOException
//import java.util.Locale
//
//class EditProfil : AppCompatActivity() {
//    private lateinit var binding: ActivityEditProfilBinding
//    private var dbProfil = Firebase.firestore
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var locationRequest: LocationRequest
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEditProfilBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        binding.addImgProfil.setOnClickListener {
//            startActivity(Intent(this, AddPhoto::class.java))
//        }
//        binding.backToProfil.setOnClickListener {
//            startActivity(Intent(this, Profil::class.java))
//        }
//        binding.btnGetLoc.setOnClickListener {
//            checkLocationPermission()
//        }
//        setData()
//
//        binding.btnSimpanProfil.setOnClickListener {
//            // Pastikan di sini URL gambar diambil terlebih dahulu sebelum menyimpan data profil
//            getImageUrlFromStorage()
//
//            // Simpan data profil seperti yang Anda lakukan sebelumnya
//            val nameProfil = binding.inpNamaProfil.text.toString().trim()
//            val noHpProfil = binding.inpNoHpProfil.text.toString().trim()
//            val emailProfil = binding.inpEmailProfil.text.toString().trim()
//            val alamatProfil = binding.inpAlamatProfil.text.toString().trim()
//
//            val profilMap = hashMapOf(
//                "namaLengkap" to nameProfil,
//                "noHpAktif" to noHpProfil,
//                "email" to emailProfil,
//                "alamatLengkap" to alamatProfil
//            )
//
//            val userId = FirebaseAuth.getInstance().currentUser!!.uid
//            dbProfil.collection("Pedagang").document(userId).set(profilMap)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Berhasil Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Gagal Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun checkLocationPermission() {
//        if (ActivityCompat.checkSelfPermission
//                (this, Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            checkGps()
//        } else {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    @SuppressLint("SuspiciousIndentation")
//    private fun checkGps() {
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
//
//        result.addOnCompleteListener { task ->
//            try {
//                val response = task.getResult(
//                    ApiException::class.java
//                )
//                getUserLocation()
//            } catch (e: ApiException) {
//                e.printStackTrace()
//                when (e.statusCode) {
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
//                        try {
//                            val resolveApiException = e as ResolvableApiException
//                            resolveApiException.startResolutionForResult(this, LOCATION_PERMISSION_REQUEST_CODE)
//                        } catch (sendIntentException: IntentSender.SendIntentException) {
//
//                        }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//
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
//        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
//            val location = task.result
//            if (location != null) {
//                try {
//                    val geocoder = Geocoder(this, Locale.getDefault())
//                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                    val addressLine = address?.get(0)?.getAddressLine(0)
//                    binding.inpCurrentLocProfil.setText(addressLine)
//                    val addressLocation = address?.get(0)?.getAddressLine(0)
//                    openLocation(addressLocation.toString())
//                    // Save user location to Firebase Realtime Database
//                    saveLocationToFirebase(location.latitude, location.longitude)
//                } catch (e: IOException) {
//
//                }
//            }
//        }
//    }
//
//    private fun openLocation(location: String) {
//        binding.inpCurrentLocProfil.setOnClickListener {
//            if (!binding.inpCurrentLocProfil.text.isNotEmpty()) {
//                val uri = Uri.parse("geo:0, 0?:=$location")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                intent.setPackage("com.google.android.apps.maps")
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun setData() {
//        val userId = FirebaseAuth.getInstance().currentUser!!.uid
//        val refProfil = dbProfil.collection("Pedagang").document(userId)
//        refProfil.get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    val nama = documentSnapshot.getString("namaLengkap")
//                    val noHp = documentSnapshot.getString("noHpAktif")
//                    val email = documentSnapshot.getString("email")
//                    val alamat = documentSnapshot.getString("alamatLengkap")
//
//                    binding.inpNamaProfil.setText(nama)
//                    binding.inpNoHpProfil.setText(noHp)
//                    binding.inpEmailProfil.setText(email)
//                    binding.inpAlamatProfil.setText(alamat)
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveImageToFirestore(imageUrl: String) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            val profilRef = dbProfil.collection("Pedagang").document(userId)
//            profilRef.update("imageUrl", imageUrl)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "URL gambar berhasil disimpan di Firestore", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { e ->
//                    Toast.makeText(this, "Gagal menyimpan URL gambar di Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun getImageUrlFromStorage() {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            val storageRef = Firebase.storage.reference.child("ProfilePedagang/$userId/FotoProfil.jpg")
//            storageRef.downloadUrl
//                .addOnSuccessListener { uri ->
//                    // URL gambar berhasil didapat
//                    val imageUrl = uri.toString()
//                    // Simpan URL gambar ke Firestore
//                    saveImageToFirestore(imageUrl)
//                }
//                .addOnFailureListener { e ->
//                    // Gagal mendapatkan URL gambar
//                    Toast.makeText(this, "Gagal mendapatkan URL gambar dari Firebase Storage: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
//        // Get the user ID
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        if (userId != null) {
//            // Get a reference to the database
//            val databaseReference = FirebaseDatabase.getInstance().reference
//
//            // Create a location object to be saved in the database
//            val locationData = hashMapOf(
//                "latitude" to latitude,
//                "longitude" to longitude
//            )
//
//            // Save the location to Firebase Realtime Database under 'userLocations/pedagang/userId'
//            databaseReference.child("userLocations").child("pedagang").child(userId).setValue(locationData)
//                .addOnSuccessListener {
//                    // Successful storage
//                    // Add appropriate actions or feedback if needed
//                    // Retrieve and display location data in a TextView if needed
//                    retrieveAndDisplayLocation()
//                }
//                .addOnFailureListener {
//                    // Failed storage
//                    // Add appropriate actions or feedback if needed
//                }
//        }
//    }
//
//    private fun retrieveAndDisplayLocation() {
//        // Get the user ID
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        if (userId != null) {
//            // Get a reference to the database
//            val databaseReference = FirebaseDatabase.getInstance().reference
//
//            // Get location data from Firebase Realtime Database
//            databaseReference.child("userLocations").child("pedagang").child(userId)
//                .get()
//                .addOnSuccessListener { dataSnapshot ->
//                    // Check if data is obtained successfully
//                    if (dataSnapshot.exists()) {
//                        // Get latitude and longitude values from the data
//                        val latitude = dataSnapshot.child("latitude").value as Double
//                        val longitude = dataSnapshot.child("longitude").value as Double
//
//                        // Display data in a TextView (e.g., inpCurrentLocProfil)
//                        binding.inpCurrentLocProfil.setText("Latitude: $latitude, Longitude: $longitude")
//                    }
//                }
//                .addOnFailureListener {
//                    // Failed to get location data
//                }
//        }
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//    }
//}
