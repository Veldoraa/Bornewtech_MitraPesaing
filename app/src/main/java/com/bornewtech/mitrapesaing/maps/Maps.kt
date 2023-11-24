package com.bornewtech.mitrapesaing.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioGroup
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.maps.Constants
import com.bornewtech.mitrapesaing.data.maps.Constants.getHeatmapData
import com.bornewtech.mitrapesaing.data.maps.FirebaseHelper
import com.bornewtech.mitrapesaing.data.maps.RealtimeLatLng

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
//    private lateinit var radioGroup: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get reference to RadioGroup
//        radioGroup = findViewById(R.id.radioGroupMaps)
//
//
//        // Set a listener for radio button changes
//        radioGroup.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.radioButton1 -> {
//                    // Handle option "3 hari Terakhir"
//                    val threeDaysAgo = System.currentTimeMillis() / 1000 - (3 * 24 * 60 * 60)
//                    addHeatmap(threeDaysAgo)
//                }
//                R.id.radioButton2 -> {
//                    // Handle option "7 Hari Terakhir"
//                    val sevenDaysAgo = System.currentTimeMillis() / 1000 - (7 * 24 * 60 * 60)
//                    addHeatmap(sevenDaysAgo)
//                }
//                R.id.radioButton3 -> {
//                    // Handle option "30 Hari Terakhir"
//                    val thirtyDaysAgo = System.currentTimeMillis() / 1000 - (30 * 24 * 60 * 60)
//                    addHeatmap(thirtyDaysAgo)
//                }
//                R.id.radioButton4 -> {
//                    // Handle option "Semua Data"
//                    addHeatmap(0) // Pass 0 or another value to retrieve all data
//                }
//            }
//        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // inisialisasi maps menu
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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_timestamp, menu )
        return true
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentTimeStamp = System.currentTimeMillis() / 1000 // Ubah ke detik
        val threeDaysAgo = currentTimeStamp - (3 * 24 * 60 * 60)
        val sevenDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
        val thirtyDaysAgo = currentTimeStamp - (7 * 24 * 60 * 60)
        val alldata = currentTimeStamp - (365 * 24 * 60 * 60)
//        when (item.itemId){
//            R.id.item1 -> addHeatmap(threeDaysAgo)
//            R.id.item2 -> addHeatmap(sevenDaysAgo)
//            R.id.item3 -> addHeatmap(thirtyDaysAgo)
//            R.id.item4 -> addHeatmap(alldata)
//        }
        when (item.itemId) {
            R.id.item1 -> {
                addHeatmap(threeDaysAgo)
                return true
            }
            R.id.item2 -> {
                addHeatmap(sevenDaysAgo)
                return true
            }
            R.id.item3 -> {
                addHeatmap(thirtyDaysAgo)

                return true
            }
            R.id.item4 -> {
                addHeatmap(alldata)
                return true
            }
            else -> return false
        }
//        return super.onOptionsItemSelected(item)
    }

    private fun addHeatmap(waktu: Long) {
        val reference = FirebaseDatabase.getInstance().reference.child("data")
//        val timestampAwal = 160000



        //      Membuat query untuk mengambil data berdasarkan timestamp
        val query = reference.orderByChild("timestamp").startAt(waktu.toDouble())

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val yourDataList = mutableListOf<RealtimeLatLng>()

                val heatmapData = ArrayList<WeightedLatLng>()

                for (snapshot in dataSnapshot.children) {
                    val cluster = snapshot.child("cluster").getValue(Int::class.java) ?: 0
                    val timestamp = snapshot.child("timestamp").getValue(Int::class.java) ?: 0
                    val jmlh = snapshot.child("jmlh").getValue(Int::class.java) ?: 0
                    val latitude = snapshot.child("lat").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("lng").getValue(Double::class.java) ?: 0.0

//                    val heatmapData = ArrayList<WeightedLatLng>()
                    heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), cluster.toDouble()))
//                    println("Data from Firebase: $heatmapData")
                }

                val heatmapProvider = HeatmapTileProvider.Builder()
                    .weightedData(heatmapData)
                    .radius(20)
                    .maxIntensity(10.0)
                    .build()

                mMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))

                // Now you have yourDataList containing the retrieved data, do something with it
                // For example, display it in a RecyclerView or update the UI
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}
