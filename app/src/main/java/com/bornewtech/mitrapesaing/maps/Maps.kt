package com.bornewtech.mitrapesaing.maps

import android.content.pm.PackageManager
import android.graphics.Color
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
import com.bornewtech.mitrapesaing.data.maps.DecodePolyline
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var myButton: Button
    private var heatmapData: ArrayList<Lokasi> = ArrayList()
    private var titikClusterTinggi: ArrayList<Lokasi> = ArrayList()
    private var radiusCircle: Circle? = null
    private lateinit var apiKey: String // Variabel untuk menyimpan kunci API
//    private var currentLocation: LatLng? = null
//    private lateinit var radioGroup: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // Mendapatkan kunci API dari strings.xml
        apiKey = getString(R.string.google_maps_api_key)

        myButton = findViewById(R.id.myButton)
        myButton.setOnClickListener {
            goToCurrentLocation()

        }

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
                        Toast.makeText(applicationContext, "Option 1 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "7 Hari" -> {
                        addHeatmap(sevenDaysAgo)
                        Toast.makeText(applicationContext, "Option 2 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "30 Hari" -> {
                        addHeatmap(thirtyDaysAgo)
                        Toast.makeText(applicationContext, "Option 3 dipilih", Toast.LENGTH_SHORT).show()
                    }
                    "Semua Data" -> {
                        addHeatmap(alldata)
                        Toast.makeText(applicationContext, "Option 4 dipilih", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Metode ini akan dipanggil saat tidak ada opsi yang dipilih (pilihan kosong)
                // Biasanya, tidak perlu melakukan tindakan khusus di sini
            }
        }

    }

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


                val titikClusterFiltered = heatmapData.filter { Lokasi ->
                    Lokasi.cluster > 5
                }

                titikClusterTinggi = titikClusterFiltered as ArrayList<Lokasi>

                val titikTerdekat = titikClusterTinggi.minByOrNull {
                    hitungJarak(currentLocation.latitude,currentLocation.longitude, it.latitude, it.longitude)
                }

                if (titikTerdekat != null) {
                    addCustomMarker(LatLng(titikTerdekat.latitude, titikTerdekat.longitude), "Titik Terdekat")
                    val geoApiContext = GeoApiContext.Builder()
                        .apiKey(apiKey)
                        .build()

                    val request: DirectionsApiRequest = DirectionsApi.newRequest(geoApiContext)
                        .origin(currentLocation.latitude.toString() + "," + currentLocation.longitude)
                        .destination(titikTerdekat!!.latitude.toString() + "," + titikTerdekat!!.longitude)
                        .mode(TravelMode.DRIVING)
                        .units(Unit.METRIC)

                    try {
                        val result: DirectionsResult = request.await()

                        if (result.routes != null && result.routes.isNotEmpty()) {
                            val route: DirectionsRoute = result.routes[0]
                            val leg: DirectionsLeg = route.legs[0]

                            val polylineOptions = PolylineOptions()
                                .addAll(PolyUtil.decode(route.overviewPolyline.encodedPath))
                                .color(Color.BLUE)
                                .width(5f)

                            mMap.addPolyline(polylineOptions)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                // Hapus lingkaran sebelum menambahkan yang baru
//                radiusCircle?.remove()

                // Tambahkan lingkaran (radius) di sekitar lokasi saat ini
//                radiusCircle = mMap.addCircle(
//                    CircleOptions()
//                        .center(currentLocation)
//                        .radius(1000.0) // Ganti dengan radius yang diinginkan dalam meter
//                        .strokeColor(Color.argb(128, 255, 0, 0)) // Warna garis lingkaran dengan transparansi
//                        .fillColor(Color.argb(128, 255, 0, 0)) // Warna isi lingkaran dengan transparansi
//                )


                val heatmapDataWithinRadius = heatmapData.filter { lokasi ->
                    hitungJarak(currentLocation.latitude, currentLocation.longitude, lokasi.latitude, lokasi.longitude) <= 100.0
                } as ArrayList<Lokasi>
                // Tampilkan heatmap hanya untuk data yang berada dalam radius lingkaran
//                showHeatmap(heatmapDataWithinRadius)

                Toast.makeText(this@Maps, "Titik Terdekat Adalah ${titikTerdekat?.latitude}, " +
                        "${titikTerdekat?.longitude}", Toast.LENGTH_SHORT).show()
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
//        mMap.setMinZoomPreference(minZoomLevel)
        // Add a marker in Sydney and move the camera
        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
        mMap.addMarker(MarkerOptions().position(pontianak).title("Marker di Pontianak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pontianak))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_timestamp, menu )
        return true
    }

    data class Lokasi(val latitude: Double, val longitude: Double, val cluster: Double)

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


    private fun showHeatmap(data: ArrayList<Lokasi>) {
        mMap.clear()

        val data = titikClusterTinggi

        if (data.isNotEmpty()) {
            val heatmapProvider = HeatmapTileProvider.Builder()
                .weightedData(data.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.cluster) })
                .radius(20)
                .maxIntensity(10.0)
                .build()

            mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
        } else {
            // Lakukan sesuatu jika heatmapDataWithinRadius kosong, seperti menampilkan pesan
            Log.d("Heatmap", "No input points available for heatmap within radius")
            Toast.makeText(this@Maps, "No input points available for heatmap within radius", Toast.LENGTH_SHORT).show()
            // ... Lakukan tindakan lainnya jika diperlukan
        }
    }

    private fun addHeatmap(waktu: Long) {
        val reference = FirebaseDatabase.getInstance().reference.child("data")

        mMap.clear()

        //      Membuat query untuk mengambil data berdasarkan timestamp
        val query = reference.orderByChild("timestamp").startAt(waktu.toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val heatmapData = ArrayList<Lokasi>()

                for (snapshot in dataSnapshot.children) {
                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0

                    heatmapData.add(Lokasi(latitude, longitude, cluster.toDouble()))
                }

                this@Maps.heatmapData = heatmapData


                // Filter titik-titik dengan nilai cluster lebih tinggi dari 5
                if (heatmapData.isNotEmpty()) {
                    val heatmapProvider = HeatmapTileProvider.Builder()
                        .weightedData(heatmapData.map { WeightedLatLng(LatLng(it.latitude, it.longitude), it.cluster) })
                        .radius(20)
                        .maxIntensity(10.0)
                        .build()

                    mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
                } else {
                    // Lakukan sesuatu jika heatmapData kosong, seperti menampilkan pesan
                    Log.d("Heatmap", "No input points available for heatmap")
                    Toast.makeText(this@Maps, "No input points available for heatmap", Toast.LENGTH_SHORT).show()
                    // ... Lakukan tindakan lainnya jika diperlukan
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Penanganan jika terjadi pembatalan
            }
        })
    }

}