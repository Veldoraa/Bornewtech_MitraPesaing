package com.bornewtech.marketplacepesaing.ui.pembeli.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.R

class DashboardPembeli : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_pembeli)
        supportActionBar?.hide()
    }
}