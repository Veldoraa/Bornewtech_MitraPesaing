package com.bornewtech.mitrapesaing.maps

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myButton: Button
//    private lateinit var radioGroup: RadioGroup

    // Declare the time variables at the class level
    private var threeDaysAgo: Long = 0
    private var sevenDaysAgo: Long = 0
    private var thirtyDaysAgo: Long = 0
    private var alldata: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        myButton = findViewById(R.id.myButton)
        myButton.setOnClickListener {
            goToCurrentLocation()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val spinner: Spinner = findViewById(R.id.dropdown_menu)

        val items = listOf("3 Hari", "7 Hari", "30 Hari", "Semua Data")
        val currentTimeStamp = System.currentTimeMillis() / 1000 // Convert to seconds
        val threeDaysAgo = currentTimeStamp - (3 * 24 * 60 * 60)
        val sevenDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
        val thirtyDaysAgo = currentTimeStamp - (30 * 24 * 60 * 60) // Corrected
        val alldata = currentTimeStamp - (365 * 24 * 60 * 60)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Menambahkan listener untuk menangani pemilihan opsi pada dropdown menu
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = items[position]
                addHeatmap(selectedItem)

                if (selectedItem == "3 Hari") {
                    addHeatmap(threeDaysAgo.toString())
                    Toast.makeText(applicationContext, "Option 1 dipilih", Toast.LENGTH_SHORT).show()
                } else if (selectedItem == "7 Hari") {
                    addHeatmap(sevenDaysAgo.toString())
                    Toast.makeText(applicationContext, "Option 2 dipilih", Toast.LENGTH_SHORT).show()
                } else if (selectedItem == "30 Hari") {
                    addHeatmap(thirtyDaysAgo.toString())
                    Toast.makeText(applicationContext, "Option 3 dipilih", Toast.LENGTH_SHORT).show()
                } else if (selectedItem == "Semua Data") {
                    addHeatmap(alldata.toString())
                    Toast.makeText(applicationContext, "Option 4 dipilih", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Metode ini akan dipanggil saat tidak ada opsi yang dipilih (pilihan kosong)
                // Biasanya, tidak perlu melakukan tindakan khusus di sini
            }
        }

    }



    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun goToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationChangeListener {
                val currentLocation = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val minZoomLevel = 13.0f
        mMap.setMinZoomPreference(minZoomLevel)
        // Add a marker in Sydney and move the camera
        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
        mMap.addMarker(MarkerOptions().position(pontianak).title("Marker di Pontianak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pontianak))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_timestamp, menu )
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val currentTimeStamp = System.currentTimeMillis() / 1000 // Ubah ke detik
//        val threeDaysAgo = currentTimeStamp - (3 * 24 * 60 * 60)
//        val sevenDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
//        val thirtyDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
//        val alldata = currentTimeStamp - (365 * 24 * 60 * 60)
//        when (item.itemId) {
//            R.id.item1 -> {
//                addHeatmap(threeDaysAgo)
//                return true
//            }
//            R.id.item2 -> {
//                addHeatmap(sevenDaysAgo)
//                return true
//            }
//            R.id.item3 -> {
//                addHeatmap(thirtyDaysAgo)
//
//                return true
//            }
//            R.id.item4 -> {
//                addHeatmap(alldata)
//                return true
//            }
//            else -> return false
//        }
//    }

    data class Lokasi(val latitude: Double, val longitude: Double)

    fun hitungJarak(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radiusBumi = 6371 // Radius Bumi dalam kilometer

        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radiusBumi * c // Jarak dalam kilometer
    }

    fun temukanTitikTerdekat(lokasiReferensi: Lokasi, listTitik: List<Lokasi>): Lokasi? {
        var titikTerdekat: Lokasi? = null
        var jarakTerdekat = Double.MAX_VALUE

        for (lokasi in listTitik) {
            val jarak = hitungJarak(
                lokasiReferensi.latitude, lokasiReferensi.longitude,
                lokasi.latitude, lokasi.longitude
            )

            if (jarak < jarakTerdekat) {
                jarakTerdekat = jarak
                titikTerdekat = lokasi
            }
        }

        return titikTerdekat
    }


    private fun addHeatmap(kategoriWaktu: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("data")

        // Membersihkan peta (hapus heatmap sebelumnya)
        mMap.clear()

        // Mendapatkan waktu berdasarkan kategori yang dipilih
        val waktu: Long = when (kategoriWaktu) {
            "3 Hari" -> threeDaysAgo
            "7 Hari" -> sevenDaysAgo
            "30 Hari" -> thirtyDaysAgo
            "Semua Data" -> alldata
            else -> 0
        }

        // Membuat query untuk mengambil data berdasarkan timestamp
        val query = reference.orderByChild("timestamp").startAt(waktu.toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val heatmapData = ArrayList<WeightedLatLng>()

                for (snapshot in dataSnapshot.children) {
                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0

                    heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), 1.0))
                }

                if (heatmapData.isNotEmpty()) {
                    // Sesuaikan pengaturan HeatmapTileProvider
                    val heatmapProvider = HeatmapTileProvider.Builder()
                        .weightedData(heatmapData)
                        .radius(50)  // Sesuaikan radius sesuai kebutuhan
                        .maxIntensity(10.0)
                        .build()

                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
                } else {
                    Log.d("Heatmap", "No input points available for heatmap")
                    Toast.makeText(this@Maps, "No input points available for heatmap", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }
}