package com.bornewtech.mitrapesaing.data.maps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng

object Constants {


    /**DUMMY DATA**/
    fun getPolylineCords(): ArrayList<LatLng> {
        return arrayListOf(
            LatLng(-0.045568, 109.366555),
            LatLng(-0.043392, 109.364330)
        )
    }

    fun getPolygonCords(): ArrayList<LatLng> {
        return arrayListOf(
            LatLng(-0.038153, 109.367337),
            LatLng(-0.032276, 109.366992)
        )
    }

    fun getHeatmapData(callback: (ArrayList<WeightedLatLng>) -> Unit) {
        val firebaseHelper = FirebaseHelper()
        firebaseHelper.getData { dataSnapshot ->
            val data = dataSnapshot.getValue(RealtimeLatLng::class.java)
            val cluster = data?.cluster ?: 0
            val jumlah = data?.jmlh ?: 0
            val latitude = data?.lat ?: 0.0
            val longitude = data?.lng ?: 0.0

            // Gunakan dataSnapshot untuk mendapatkan data dari Firebase
            // Contoh: dataSnapshot.getValue(NamaKelas::class.java)
            val heatmapData = ArrayList<WeightedLatLng>()
            heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), cluster.toDouble()))

            // Call the callback with the result
            callback(heatmapData)
        }
    }

}
