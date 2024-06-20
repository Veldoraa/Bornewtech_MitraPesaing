package com.bornewtech.mitrapesaing.maps

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.google.maps.model.DirectionsLeg
import com.google.maps.model.DirectionsResult
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import com.google.maps.model.Unit
import com.google.maps.android.PolyUtil
import com.google.maps.android.heatmaps.Gradient
import java.util.Calendar
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var calendarButton: Button
    private lateinit var myButton: Button
    //    private lateinit var myToGoogleMaps: Button
    private var heatmapData: ArrayList<Lokasi> = ArrayList()
    private var titikClusterTinggi: ArrayList<Lokasi> = ArrayList()
    //    private var radiusCircle: Circle? = null
    private lateinit var apiKey: String // Variabel untuk menyimpan kunci API
    private var isSelectingEndDate = false
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // Mendapatkan kunci API dari strings.xml
        apiKey = getString(R.string.google_maps_api_key)

        myButton = findViewById(R.id.myButton)
        myButton.setOnClickListener {
            goToCurrentLocation()
        }

        calendarButton = findViewById(R.id.calendar_button)
        calendarButton.setOnClickListener {
            openCalendar()
        }


        // Tombol kalender
//        calendarButton = findViewById(R.id.calendar_button)
//        calendarButton.setOnClickListener {
//            openCalendar()
//        }

//        myToGoogleMaps = findViewById(R.id.myToGoogleMaps)
//        myToGoogleMaps.setOnClickListener {
//            val currentLocation = mMap.myLocation
//            val titikTerdekat = titikClusterTinggi.minByOrNull {
//                hitungJarak(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0, it.latitude, it.longitude)
//            }
//
//            if (titikTerdekat != null) {
//                openGoogleMaps(currentLocation, titikTerdekat)
//            } else {
//                Toast.makeText(this@Maps, "No nearest point found", Toast.LENGTH_SHORT).show()
//            }
//        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val spinner: Spinner = findViewById(R.id.dropdown_menu)

        val items = listOf("3 Hari", "7 Hari", "30 Hari", "Semua Data")
        val currentTimeStamp = System.currentTimeMillis() / 1000 // Ubah ke detik
        val threeDaysAgo = currentTimeStamp - (3 * 24 * 60 * 60)
        val sevenDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
        val thirtyDaysAgo = currentTimeStamp - (30 * 24 * 60 * 60)
        val alldata: Long = 1696118400

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

                when (selectedItem) {
                    "3 Hari" -> {
                        addHeatmap(threeDaysAgo)
                        Toast.makeText(applicationContext, "Kategori 1 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "7 Hari" -> {
                        addHeatmap(sevenDaysAgo)
                        Toast.makeText(applicationContext, "Kategori 2 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "30 Hari" -> {
                        addHeatmap(thirtyDaysAgo)
                        Toast.makeText(applicationContext, "Kategori 3 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "Semua Data" -> {
                        addHeatmap(alldata)
                        Toast.makeText(applicationContext, "Kategori 4 dipilih", Toast.LENGTH_SHORT).show()
                    }
//                    "Pilih Tanggal" -> {
//                        if (!isSelectingEndDate) {
//                            openCalendar()
//                            isSelectingEndDate = true
//                            Toast.makeText(applicationContext, "Pilih tanggal awal", Toast.LENGTH_SHORT).show()
//                        } else {
//                            isSelectingEndDate = false
//                        }
//                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Metode ini akan dipanggil saat tidak ada opsi yang dipilih (pilihan kosong)
                // Biasanya, tidak perlu melakukan tindakan khusus di sini
            }
        }

    }

    // kalender
    private fun openCalendar(title: String = "Tanggal Mulai") { // Memberikan nilai default "Tanggal Mulai" untuk parameter title
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                if (startDate == null) {
                    startDate = selectedDate.time
                    openCalendar("Tanggal Akhir") // Buka kembali kalender untuk memilih tanggal akhir
                } else {
                    endDate = selectedDate.time
                    // Lakukan sesuatu dengan rentang tanggal yang dipilih, misalnya memperbarui heatmap
                    fetchHeatmapDataByDateRange(startDate!!, endDate!!)
                    startDate = null
                    endDate = null
                }
            },
            year, month, day
        )
        // Jika sudah memilih tanggal awal, atur tanggal minimal untuk tanggal akhir
        datePickerDialog.setTitle(title)
        if (startDate != null) {
            datePickerDialog.datePicker.minDate = startDate!!.time
        }
        datePickerDialog.show()
    }



    private fun fetchHeatmapDataByDateRange(startDate: Date, endDate: Date) {
        // Konversi tanggal awal ke timestamp
        val startTimestamp = startDate.time / 1000 // dalam detik
        val endTimestamp = endDate.time / 1000 // dalam detik

        // Gunakan timestamp untuk mengambil data dari Firebase
        val reference = FirebaseDatabase.getInstance().reference.child("data")
        val heatmapData = ArrayList<Lokasi>()
        mMap.clear()

        val query = reference.orderByChild("timestamp")
            .startAt(startTimestamp.toDouble())
            .endAt(endTimestamp.toDouble())

        // Define colors for the gradient
        val colors = intArrayOf(
            Color.GREEN,  // Starting color (for lower weights)
            Color.YELLOW, // Intermediate color
            Color.RED     // Ending color (for higher weights)
        )

        // Define the start points for the colors (0..1)
        val startPoints = floatArrayOf(0.4f, 0.6f, 1.0f)

        // Create the gradient
        val gradient = Gradient(colors, startPoints)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                heatmapData.clear()

                for (snapshot in dataSnapshot.children) {
                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0
                    val weight = snapshot.child("weight").getValue(Double::class.java) ?: 0.0

                    heatmapData.add(Lokasi(latitude, longitude, weight, cluster))
                }



                this@Maps.heatmapData = heatmapData

                if (heatmapData.isNotEmpty()) {
                    val heatmapProvider = HeatmapTileProvider.Builder()
                        .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
                        .gradient(gradient)
                        .radius(20)
                        .maxIntensity(10.0)
                        .build()
                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
                } else {
                    Toast.makeText(this@Maps, "Tidak ada data untuk tanggal ini", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    } //

    //tambah marker
    private fun addCustomMarker(position: LatLng, title: String) {
        mMap.addMarker(MarkerOptions().position(position).title(title))
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

    // Define a function to cluster the points based on a certain distance
    fun clusterPoints(points: List<Lokasi>, center: LatLng, radius: Double): List<List<Lokasi>> {
        val clusters = mutableListOf<MutableList<Lokasi>>()

        points.forEach { point ->
            var added = false
            clusters.forEach { cluster ->
                if (hitungJarak(point.latitude, point.longitude, center.latitude, center.longitude) <= radius) {
                    cluster.add(point)
                    added = true
                }
            }
            if (!added) {
                val newCluster = mutableListOf(point)
                clusters.add(newCluster)
            }
        }

        return clusters
    }


    @SuppressLint("PotentialBehaviorOverride")
    private fun goToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationChangeListener { location ->
                val currentLocation = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

                val titikClusterFiltered = heatmapData.filter { Lokasi ->
                    Lokasi.weight > 1
                }

                titikClusterTinggi = titikClusterFiltered as ArrayList<Lokasi>

                val titikTerdekat = titikClusterTinggi.minByOrNull {
                    hitungJarak(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        it.latitude,
                        it.longitude
                    )
                }

                if (titikTerdekat != null) {
                    // Hapus semua marker sebelum menambahkan yang baru
                    mMap.clear()
                    // Cluster the points within a 500-meter radius into separate clusters
                    val clusteredPoints = clusterPoints(titikClusterTinggi, currentLocation, 500.0)

                    // Tambahkan marker untuk titik terdekat
//                    addCustomMarker(
//                        LatLng(titikTerdekat.latitude, titikTerdekat.longitude),
//                        "Titik Terdekat"
//                    )

                    // Get the 3 nearest points within a 500-meter radius
                    val nearestPoints = titikClusterTinggi.filter {
                        hitungJarak(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            it.latitude,
                            it.longitude
                        ) <= 500.0
                    }.sortedBy {
                        hitungJarak(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            it.latitude,
                            it.longitude
                        )
                    }.take(5)


                    // Add markers for the 5 nearest points
                    addMarkersForNearestPoints(currentLocation, nearestPoints)

                    // Set marker click listener
                    mMap.setOnMarkerClickListener { marker ->
                        val selectedLocation = heatmapData.find {
                            it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
                        }
                        showSelectedMarkerInfo(selectedLocation)
                        true
                    }

                    // Add heatmap after adding markers
                    addHeatmapOverlay()
                }

                Toast.makeText(
                    this@Maps,
                    "Titik Terdekat Adalah ${titikTerdekat?.latitude}, " +
                            "${titikTerdekat?.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearAllMarkers() {
        // Hapus semua marker pada peta
        mMap.clear()
        // Tambahkan kembali heatmap setelah menghapus marker
        addHeatmapOverlay()
    }




    private fun addHeatmapOverlay() {
        // Add heatmap after adding markers
        if (heatmapData.isNotEmpty()) {
            val heatmapProvider = HeatmapTileProvider.Builder()
                .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
                .radius(20)
                .maxIntensity(10.0)
                .build()

            mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
        } else {
            // Lakukan sesuatu jika heatmapData kosong, seperti menampilkan pesan
            Log.d("Heatmap", "No input points available for heatmap")
            Toast.makeText(this@Maps, "Titik Point Peta Panas Tidak termuat di Kategori Ini", Toast.LENGTH_SHORT).show()
            // ... Lakukan tindakan lainnya jika diperlukan
        }
    }

    private fun addMarkersForNearestPoints(
        currentLocation: LatLng,
        nearestPoints: List<Lokasi>
    ) {
        for ((index, nearestPoint) in nearestPoints.withIndex()) {
            val markerOptions = MarkerOptions()
                .position(LatLng(nearestPoint.latitude, nearestPoint.longitude))
                .title("Titik Terdekat ${index + 1}")
            mMap.addMarker(markerOptions)
        }
    }


    private fun showSelectedMarkerInfo(selectedLocation: Lokasi?) {
        // Handle the click event for the selected marker, e.g., show details
        if (selectedLocation != null) {
            val currentLocation = mMap. myLocation
            val alertDialogBuilder = AlertDialog.Builder(this)
            val distance = hitungJarak(currentLocation.latitude, currentLocation.longitude, selectedLocation.latitude, selectedLocation.longitude)
            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1)


            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses?.get(0)
                    if (address != null) {
                        val roundedDistance = Math.round(distance * 1000 * 100.0) / 100.0
                        alertDialogBuilder.setTitle("Lokasi Yang Anda Pilih")
//                        alertDialogBuilder.setMessage("Latitude: ${selectedLocation.latitude}, Longitude: ${selectedLocation.longitude}\nAddress: ${address.getAddressLine(0)}")
                        val message = "<b>Jarak menuju titik tersebut adalah: ${roundedDistance} Meter.</b><br/>Jalan: ${address.getAddressLine(0)}"
                        alertDialogBuilder.setMessage(Html.fromHtml(message))
                        alertDialogBuilder.setPositiveButton("Menuju Rute") { _, _ ->
                            // Open Google Maps for navigation
                            navigateToSelectedLocation(selectedLocation)
                        }
                        alertDialogBuilder.setNegativeButton("Batal") { dialog, _ ->
                            dialog.dismiss()
                        }
                        alertDialogBuilder.create().show()
                    }
                } else {
                    Log.d("ReverseGeocode", "No address found")
                }
            }

        }
    }

    private fun navigateToSelectedLocation(location: Lokasi) {
        val currentLocation = mMap.myLocation
        val uri = "https://www.google.com/maps/dir/?api=1&origin=${currentLocation?.latitude},${currentLocation?.longitude}" +
                "&destination=${location.latitude},${location.longitude}&travelmode=driving"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun openGoogleMaps(startLocation: Location?, destination: Lokasi) {
//        val uri = "https://www.google.com/maps/dir/?api=1&origin=${startLocation?.latitude},${startLocation?.longitude}" +
//                "&destination=${destination.latitude},${destination.longitude}&travelmode=driving"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//        intent.setPackage("com.google.android.apps.maps")
//
//        try {
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(this, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
//        }
//    }

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
                Toast.makeText(this, "Izin Lokasi Di Tolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val minZoomLevel = 13.0f
        // mMap.setMinZoomPreference(minZoomLevel)
        // Add a marker in Pontianak and move the camera
        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
        mMap.addMarker(MarkerOptions().position(pontianak).title("Marker in Pontianak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pontianak))
        mMap.setMinZoomPreference(minZoomLevel)

        // Set marker click listener
        mMap.setOnMarkerClickListener { marker ->
            val selectedLocation = heatmapData.find {
                it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
            }
            showSelectedMarkerInfo(selectedLocation)
            true
        }
    }



    data class Lokasi(val latitude: Double, val longitude: Double, val weight: Double, val cluster: Int)

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

    private fun addHeatmap(waktu: Long) {
        val reference = FirebaseDatabase.getInstance().reference.child("data")
        val heatmapData = ArrayList<Lokasi>()
        mMap.clear()

        //      Membuat query untuk mengambil data berdasarkan timestamp
        val query = reference.orderByChild("timestamp").startAt(waktu.toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                for (snapshot in dataSnapshot.children) {
                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0
                    val weight = snapshot.child("weight").getValue(Double::class.java) ?: 0.0

                    heatmapData.add(Lokasi(latitude, longitude, weight, cluster))
                }

                this@Maps.heatmapData = heatmapData

                if (heatmapData.isNotEmpty()) {
                    val colors = intArrayOf(
                        Color.rgb(0, 255, 0),    // Green
                        Color.rgb(255, 255, 0),  // Yellow
                        Color.rgb(255, 0, 0)     // Red
                    )

                    // Define the start points for the gradient colors
                    val startPoints = floatArrayOf(0.4f, 0.6f, 1.0f)

                    // Create the gradient
                    val gradient = Gradient(colors, startPoints)

                    val heatmapProvider = HeatmapTileProvider.Builder()
                        .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
                        .gradient(gradient)
                        .radius(20)
                        .maxIntensity(10.0)
                        .build()
                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
                } else {
                    Log.d("Heatmap", "No input points available for heatmap")
                    Toast.makeText(this@Maps, "No input points available for heatmap", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi pembatalan
            }
        })
    }

}











//package com.bornewtech.mitrapesaing.maps
//
//import android.annotation.SuppressLint
//import android.app.DatePickerDialog
//import android.content.ActivityNotFoundException
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.location.Geocoder
//import android.location.Location
//import android.net.Uri
//import android.os.Bundle
//import android.text.Html
//import android.util.Log
//import android.view.Menu
//import android.widget.Button
//import android.widget.Spinner
//import android.widget.ArrayAdapter
//import android.widget.AdapterView
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.databinding.ActivityMapsBinding
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.Circle
//import com.google.android.gms.maps.model.CircleOptions
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.gms.maps.model.PolylineOptions
//import com.google.android.gms.maps.model.TileOverlayOptions
//import com.google.firebase.FirebaseApp
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.maps.DirectionsApi
//import com.google.maps.DirectionsApiRequest
//import com.google.maps.GeoApiContext
//import com.google.maps.android.heatmaps.HeatmapTileProvider
//import com.google.maps.android.heatmaps.WeightedLatLng
//import com.google.maps.model.DirectionsLeg
//import com.google.maps.model.DirectionsResult
//import com.google.maps.model.DirectionsRoute
//import com.google.maps.model.TravelMode
//import com.google.maps.model.Unit
//import com.google.maps.android.PolyUtil
//import java.util.Calendar
//import java.util.Date
//import kotlin.math.atan2
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.math.sqrt
//
//
//class Maps : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var mMap: GoogleMap
//    private lateinit var binding: ActivityMapsBinding
//    private lateinit var calendarButton: Button
//    private lateinit var myButton: Button
////    private lateinit var myToGoogleMaps: Button
//    private var heatmapData: ArrayList<Lokasi> = ArrayList()
//    private var titikClusterTinggi: ArrayList<Lokasi> = ArrayList()
////    private var radiusCircle: Circle? = null
//    private lateinit var apiKey: String // Variabel untuk menyimpan kunci API
//    private var isSelectingEndDate = false
//    private var startDate: Date? = null
//    private var endDate: Date? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMapsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        // Inisialisasi Firebase
//        FirebaseApp.initializeApp(this)
//
//        // Mendapatkan kunci API dari strings.xml
//        apiKey = getString(R.string.google_maps_api_key)
//
//        myButton = findViewById(R.id.myButton)
//        myButton.setOnClickListener {
//            goToCurrentLocation()
//        }
//
//        // Tombol kalender
////        calendarButton = findViewById(R.id.calendar_button)
////        calendarButton.setOnClickListener {
////            openCalendar()
////        }
//
////        myToGoogleMaps = findViewById(R.id.myToGoogleMaps)
////        myToGoogleMaps.setOnClickListener {
////            val currentLocation = mMap.myLocation
////            val titikTerdekat = titikClusterTinggi.minByOrNull {
////                hitungJarak(currentLocation?.latitude ?: 0.0, currentLocation?.longitude ?: 0.0, it.latitude, it.longitude)
////            }
////
////            if (titikTerdekat != null) {
////                openGoogleMaps(currentLocation, titikTerdekat)
////            } else {
////                Toast.makeText(this@Maps, "No nearest point found", Toast.LENGTH_SHORT).show()
////            }
////        }
//
//
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        val spinner: Spinner = findViewById(R.id.dropdown_menu)
//
//        val items = listOf("3 Hari", "7 Hari", "30 Hari", "Semua Data", "Pilih Tanggal")
//        val currentTimeStamp = System.currentTimeMillis() / 1000 // Ubah ke detik
//        val threeDaysAgo = currentTimeStamp - (3 * 24 * 60 * 60)
//        val sevenDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
//        val thirtyDaysAgo = currentTimeStamp - (30 * 24 * 60 * 60)
//        val alldata: Long = 1696118400
//
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        // Menambahkan listener untuk menangani pemilihan opsi pada dropdown menu
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedItem = items[position]
//
//                when (selectedItem) {
//                    "3 Hari" -> {
//                        addHeatmap(threeDaysAgo)
//                        Toast.makeText(applicationContext, "Kategori 1 dipilih", Toast.LENGTH_SHORT).show()
//                    }
//                    "7 Hari" -> {
//                        addHeatmap(sevenDaysAgo)
//                        Toast.makeText(applicationContext, "Kategori 2 dipilih", Toast.LENGTH_SHORT).show()
//                    }
//                    "30 Hari" -> {
//                        addHeatmap(thirtyDaysAgo)
//                        Toast.makeText(applicationContext, "Kategori 3 dipilih", Toast.LENGTH_SHORT).show()
//                    }
//                    "Semua Data" -> {
//                        addHeatmap(alldata)
//                        Toast.makeText(applicationContext, "Kategori 4 dipilih", Toast.LENGTH_SHORT).show()
//                    }
//                    "Pilih Tanggal" -> {
//                        if (!isSelectingEndDate) {
//                            openCalendar()
//                            isSelectingEndDate = true
//                            Toast.makeText(applicationContext, "Pilih tanggal awal", Toast.LENGTH_SHORT).show()
//                        } else {
//                            isSelectingEndDate = false
//                        }
//                    }
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Metode ini akan dipanggil saat tidak ada opsi yang dipilih (pilihan kosong)
//                // Biasanya, tidak perlu melakukan tindakan khusus di sini
//            }
//        }
//
//    }
//
//    // kalender
//    private fun openCalendar() {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePickerDialog = DatePickerDialog(
//            this,
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val selectedDate = Calendar.getInstance()
//                selectedDate.set(selectedYear, selectedMonth, selectedDay)
//                if (startDate == null) {
//                    startDate = selectedDate.time
//                    openCalendar() // Buka kembali kalender untuk memilih tanggal akhir
//                } else {
//                    endDate = selectedDate.time
//                    fetchHeatmapDataByDateRange(startDate!!, endDate!!)
//                    startDate = null
//                    endDate = null
//                }
//            },
//            year, month, day
//        )
//        if (startDate == null) {
//            datePickerDialog.show()
//        } else {
//            datePickerDialog.datePicker.minDate = startDate!!.time
//            datePickerDialog.show()
//        }
//    }
//
//    private fun fetchHeatmapDataByDateRange(startDate: Date, endDate: Date) {
//        // Konversi tanggal awal ke timestamp
//        val startTimestamp = startDate.time / 1000 // dalam detik
//        val endTimestamp = endDate.time / 1000 // dalam detik
//
//        // Gunakan timestamp untuk mengambil data dari Firebase
//        val reference = FirebaseDatabase.getInstance().reference.child("data")
//        val heatmapData = ArrayList<Lokasi>()
//        mMap.clear()
//
//        val query = reference.orderByChild("timestamp")
//            .startAt(startTimestamp.toDouble())
//            .endAt(endTimestamp.toDouble())
//
//
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                heatmapData.clear()
//
//                for (snapshot in dataSnapshot.children) {
//                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
//                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
//                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0
//                    val weight = snapshot.child("weight").getValue(Double::class.java) ?: 0.0
//
//                    heatmapData.add(Lokasi(latitude, longitude, weight, cluster))
//                }
//
//                this@Maps.heatmapData = heatmapData
//
//                if (heatmapData.isNotEmpty()) {
//                    val heatmapProvider = HeatmapTileProvider.Builder()
//                        .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
//                        .radius(20)
//                        .maxIntensity(10.0)
//                        .build()
//                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
//                } else {
//                    Toast.makeText(this@Maps, "Tidak ada data untuk tanggal ini", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Firebase", "Error: ${error.message}")
//            }
//        })
//    } //
//
//    //tambah marker
//    private fun addCustomMarker(position: LatLng, title: String) {
//        mMap.addMarker(MarkerOptions().position(position).title(title))
//    }
//    private fun enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    // Define a function to cluster the points based on a certain distance
//    fun clusterPoints(points: List<Lokasi>, center: LatLng, radius: Double): List<List<Lokasi>> {
//        val clusters = mutableListOf<MutableList<Lokasi>>()
//
//        points.forEach { point ->
//            var added = false
//            clusters.forEach { cluster ->
//                if (hitungJarak(point.latitude, point.longitude, center.latitude, center.longitude) <= radius) {
//                    cluster.add(point)
//                    added = true
//                }
//            }
//            if (!added) {
//                val newCluster = mutableListOf(point)
//                clusters.add(newCluster)
//            }
//        }
//
//        return clusters
//    }
//
//
//    @SuppressLint("PotentialBehaviorOverride")
//    private fun goToCurrentLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//            mMap.setOnMyLocationChangeListener { location ->
//                val currentLocation = LatLng(location.latitude, location.longitude)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
//
//                val titikClusterFiltered = heatmapData.filter { Lokasi ->
//                    Lokasi.weight > 10
//                }
//
//                titikClusterTinggi = titikClusterFiltered as ArrayList<Lokasi>
//
//                val titikTerdekat = titikClusterTinggi.minByOrNull {
//                    hitungJarak(
//                        currentLocation.latitude,
//                        currentLocation.longitude,
//                        it.latitude,
//                        it.longitude
//                    )
//                }
//
//                if (titikTerdekat != null) {
//                    // Hapus semua marker sebelum menambahkan yang baru
//                    mMap.clear()
//                    // Cluster the points within a 500-meter radius into separate clusters
//                    val clusteredPoints = clusterPoints(titikClusterTinggi, currentLocation, 500.0)
//
//                    // Tambahkan marker untuk titik terdekat
////                    addCustomMarker(
////                        LatLng(titikTerdekat.latitude, titikTerdekat.longitude),
////                        "Titik Terdekat"
////                    )
//
//                    // Get the 3 nearest points within a 500-meter radius
//                    val nearestPoints = titikClusterTinggi.filter {
//                        hitungJarak(
//                            currentLocation.latitude,
//                            currentLocation.longitude,
//                            it.latitude,
//                            it.longitude
//                        ) <= 500.0
//                    }.sortedBy {
//                        hitungJarak(
//                            currentLocation.latitude,
//                            currentLocation.longitude,
//                            it.latitude,
//                            it.longitude
//                        )
//                    }.take(5)
//
//
//                    // Add markers for the 5 nearest points
//                    addMarkersForNearestPoints(currentLocation, nearestPoints)
//
//                    // Set marker click listener
//                    mMap.setOnMarkerClickListener { marker ->
//                        val selectedLocation = heatmapData.find {
//                            it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
//                        }
//                        showSelectedMarkerInfo(selectedLocation)
//                        true
//                    }
//
//                    // Add heatmap after adding markers
//                    addHeatmapOverlay()
//                }
//
//                Toast.makeText(
//                    this@Maps,
//                    "Titik Terdekat Adalah ${titikTerdekat?.latitude}, " +
//                            "${titikTerdekat?.longitude}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } else {
//            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun clearAllMarkers() {
//        // Hapus semua marker pada peta
//        mMap.clear()
//        // Tambahkan kembali heatmap setelah menghapus marker
//        addHeatmapOverlay()
//    }
//
//
//
//
//    private fun addHeatmapOverlay() {
//        // Add heatmap after adding markers
//        if (heatmapData.isNotEmpty()) {
//            val heatmapProvider = HeatmapTileProvider.Builder()
//                .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
//                .radius(20)
//                .maxIntensity(10.0)
//                .build()
//
//            mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
//        } else {
//            // Lakukan sesuatu jika heatmapData kosong, seperti menampilkan pesan
//            Log.d("Heatmap", "No input points available for heatmap")
//            Toast.makeText(this@Maps, "Titik Point Peta Panas Tidak termuat di Kategori Ini", Toast.LENGTH_SHORT).show()
//            // ... Lakukan tindakan lainnya jika diperlukan
//        }
//    }
//
//    private fun addMarkersForNearestPoints(
//        currentLocation: LatLng,
//        nearestPoints: List<Lokasi>
//    ) {
//        for ((index, nearestPoint) in nearestPoints.withIndex()) {
//            val markerOptions = MarkerOptions()
//                .position(LatLng(nearestPoint.latitude, nearestPoint.longitude))
//                .title("Titik Terdekat ${index + 1}")
//            mMap.addMarker(markerOptions)
//        }
//    }
//
//
//    private fun showSelectedMarkerInfo(selectedLocation: Lokasi?) {
//        // Handle the click event for the selected marker, e.g., show details
//        if (selectedLocation != null) {
//            val currentLocation = mMap. myLocation
//            val alertDialogBuilder = AlertDialog.Builder(this)
//            val distance = hitungJarak(currentLocation.latitude, currentLocation.longitude, selectedLocation.latitude, selectedLocation.longitude)
//            val geocoder = Geocoder(this)
//            val addresses = geocoder.getFromLocation(selectedLocation.latitude, selectedLocation.longitude, 1)
//
//
//            if (addresses != null) {
//                if (addresses.isNotEmpty()) {
//                    val address = addresses?.get(0)
//                    if (address != null) {
//                        val roundedDistance = Math.round(distance * 1000 * 100.0) / 100.0
//                        alertDialogBuilder.setTitle("Lokasi Yang Anda Pilih")
////                        alertDialogBuilder.setMessage("Latitude: ${selectedLocation.latitude}, Longitude: ${selectedLocation.longitude}\nAddress: ${address.getAddressLine(0)}")
//                        val message = "<b>Jarak menuju titik tersebut adalah: ${roundedDistance} Meter.</b><br/>Jalan: ${address.getAddressLine(0)}"
//                        alertDialogBuilder.setMessage(Html.fromHtml(message))
//                        alertDialogBuilder.setPositiveButton("Menuju Rute") { _, _ ->
//                            // Open Google Maps for navigation
//                            navigateToSelectedLocation(selectedLocation)
//                        }
//                        alertDialogBuilder.setNegativeButton("Batal") { dialog, _ ->
//                            dialog.dismiss()
//                        }
//                        alertDialogBuilder.create().show()
//                    }
//                } else {
//                    Log.d("ReverseGeocode", "No address found")
//                }
//            }
//
//        }
//    }
//
//    private fun navigateToSelectedLocation(location: Lokasi) {
//        val currentLocation = mMap.myLocation
//        val uri = "https://www.google.com/maps/dir/?api=1&origin=${currentLocation?.latitude},${currentLocation?.longitude}" +
//                "&destination=${location.latitude},${location.longitude}&travelmode=driving"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//        intent.setPackage("com.google.android.apps.maps")
//
//        try {
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(this, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
////    private fun openGoogleMaps(startLocation: Location?, destination: Lokasi) {
////        val uri = "https://www.google.com/maps/dir/?api=1&origin=${startLocation?.latitude},${startLocation?.longitude}" +
////                "&destination=${destination.latitude},${destination.longitude}&travelmode=driving"
////        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
////        intent.setPackage("com.google.android.apps.maps")
////
////        try {
////            startActivity(intent)
////        } catch (e: ActivityNotFoundException) {
////            Toast.makeText(this, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
////        }
////    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableMyLocation()
//            } else {
//                Toast.makeText(this, "Izin Lokasi Di Tolak", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//    }
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        val minZoomLevel = 13.0f
//        // mMap.setMinZoomPreference(minZoomLevel)
//        // Add a marker in Pontianak and move the camera
//        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
//        mMap.addMarker(MarkerOptions().position(pontianak).title("Marker in Pontianak"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(pontianak))
//        mMap.setMinZoomPreference(minZoomLevel)
//
//        // Set marker click listener
//        mMap.setOnMarkerClickListener { marker ->
//            val selectedLocation = heatmapData.find {
//                it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
//            }
//            showSelectedMarkerInfo(selectedLocation)
//            true
//        }
//    }
//
//
//
//    data class Lokasi(val latitude: Double, val longitude: Double, val weight: Double, val cluster: Int)
//
//    fun hitungJarak(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//        val radiusBumi = 6371 // Radius Bumi dalam kilometer
//
//        val deltaLat = Math.toRadians(lat2 - lat1)
//        val deltaLon = Math.toRadians(lon2 - lon1)
//        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
//                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
//                sin(deltaLon / 2) * sin(deltaLon / 2)
//        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
//
//        return radiusBumi * c // Jarak dalam kilometer
//    }
//
//    private fun addHeatmap(waktu: Long) {
//        val reference = FirebaseDatabase.getInstance().reference.child("data")
//        val heatmapData = ArrayList<Lokasi>()
//        mMap.clear()
//
//        //      Membuat query untuk mengambil data berdasarkan timestamp
//        val query = reference.orderByChild("timestamp").startAt(waktu.toDouble())
//
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//
//                for (snapshot in dataSnapshot.children) {
//                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
//                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
//                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0
//                    val weight = snapshot.child("weight").getValue(Double::class.java) ?: 0.0
//
//                    heatmapData.add(Lokasi(latitude, longitude, weight, cluster))
//                }
//
//                this@Maps.heatmapData = heatmapData
//
//                if (heatmapData.isNotEmpty()) {
//                    val heatmapProvider = HeatmapTileProvider.Builder()
//                        .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.weight) })
//                        .radius(20)
//                        .maxIntensity(10.0)
//                        .build()
//                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
//                } else {
//                    Log.d("Heatmap", "No input points available for heatmap")
//                    Toast.makeText(this@Maps, "No input points available for heatmap", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Penanganan jika terjadi pembatalan
//            }
//        })
//    }
//
//}