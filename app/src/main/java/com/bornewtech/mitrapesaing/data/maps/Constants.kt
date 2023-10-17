package com.bornewtech.mitrapesaing.data.maps

import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

object Constants {

    /**DUMMY DATA**/
    fun getPolylineCords():ArrayList<LatLng> {
        return arrayListOf(
            LatLng(-0.045568, 109.366555),
            LatLng(-0.043392, 109.364330)
        )
    }

    fun getPolygonCords():ArrayList<LatLng>{
        return arrayListOf(
            LatLng(-0.038153, 109.367337),
            LatLng(-0.032276, 109.366992)
        )
    }

    fun getHeatmapData():ArrayList<LatLng>{
        val heatmapData = ArrayList<LatLng>()
        for (i in -1..200) {
            val latitude = Random.nextDouble(-1.0, 110.0)
            val longitude = Random.nextDouble(-1.0, 110.0)
            heatmapData.add(LatLng(latitude, longitude))
        }
        return heatmapData
    }
}