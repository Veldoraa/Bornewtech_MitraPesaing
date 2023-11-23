package com.bornewtech.mitrapesaing.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bornewtech.mitrapesaing.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bornewtech.mitrapesaing.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import java.util.Calendar
import kotlin.math.log


class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val firebaseHelper = FirebaseHelper()

//        firebaseHelper.getData { dataSnapshot -> val data = dataSnapshot.getValue(RealtimeLatLng::class.java)
//            val cluster = data?.cluster ?: 0
//            val jumlah = data?.jmlh ?: 0
//            val latitude = data?.lat ?: 0.0
//            val longitude = data?.lng ?: 0.0
//            // Gunakan dataSnapshot untuk mendapatkan data dari Firebase
//            // Contoh: dataSnapshot.getValue(NamaKelas::class.java)
//        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
        mMap.addMarker(MarkerOptions().position(pontianak).title("Marker di Pontianak"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pontianak))
        addHeatmap()
    }

    private var time: Int = 0
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_timestamp, menu )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> {
                time = 3
                addHeatmap()
            }
            R.id.item2 -> {
                time = 7
                addHeatmap()
            }
            R.id.item3 -> {
                time = 30
                addHeatmap()
            }
            R.id.item4 -> {
                time = 365
                addHeatmap()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addHeatmap(time: Int = 0) {
        val reference = FirebaseDatabase.getInstance().reference.child("data")
        val fewDaysAgoMillis = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -time) // Use -time here to subtract days
        }.timeInMillis

        Log.d("MapsActivity", "Start timestamp: $fewDaysAgoMillis")

        reference.orderByChild("timestamp").startAt(fewDaysAgoMillis.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val heatmapData = ArrayList<WeightedLatLng>()

                for (snapshot in dataSnapshot.children) {
                    val cluster = snapshot.child("cluster").getValue(Int::class.java)
                    val jmlh = snapshot.child("jmlh").getValue(Int::class.java)
                    val latitude = snapshot.child("lat").getValue(Double::class.java)
                    val longitude = snapshot.child("lng").getValue(Double::class.java)

                    if (cluster != null && jmlh != null && latitude != null && longitude != null) {
                        heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), cluster.toDouble()))
                    } else {
                        Log.e("MapsActivity", "One or more values from Firebase are null.")
                    }
                }

                runOnUiThread {
                    if (heatmapData.isNotEmpty()) {
                        val heatmapProvider = HeatmapTileProvider.Builder()
                            .weightedData(heatmapData)
                            .radius(20)
                            .maxIntensity(10.0)
                            .build()

                        mMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
                    } else {
                        Log.e("MapsActivity", "No data available for the selected time range.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapsActivity", "Firebase query cancelled: $error")
            }
        })
    }

//        FirebaseApp.initializeApp(this)
//
//        getHeatmapData { heatmapData ->
//            // Now you have the heatmapData, you can use it here
//            // For example, update your UI or perform other actions
//            // with the heatmapData
//            // Example: Update UI with the heatmapData
//            val heatmapProvider = HeatmapTileProvider.Builder()
//                .weightedData(heatmapData)
//                .radius(20)
//                .maxIntensity(25.0)
//                .build()
//
//            mMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
//        }
//        val heatmapProvider = HeatmapTileProvider.Builder()
//            .weightedData(Constants.getHeatmapData())
//            .radius(20)
//            .maxIntensity(25.0)
//            .build()
//        mMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))

}