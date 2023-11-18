package com.bornewtech.mitrapesaing.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.google.maps.android.heatmaps.HeatmapTileProvider


class Maps : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    private fun addHeatmap(){
        FirebaseApp.initializeApp(this)

        getHeatmapData { heatmapData ->
            // Now you have the heatmapData, you can use it here
            // For example, update your UI or perform other actions
            // with the heatmapData
            // Example: Update UI with the heatmapData
            val heatmapProvider = HeatmapTileProvider.Builder()
                .weightedData(heatmapData)
                .radius(20)
                .maxIntensity(25.0)
                .build()

            mMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
        }
//        val heatmapProvider = HeatmapTileProvider.Builder()
//            .weightedData(Constants.getHeatmapData())
//            .radius(20)
//            .maxIntensity(25.0)
//            .build()
//        mMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
    }


}