package com.bornewtech.marketplacepesaing.main

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.maps.MapsPesaing
import com.bornewtech.marketplacepesaing.ui.login.LoginPedagang
import com.bornewtech.marketplacepesaing.ui.pedagang.barang.DetailBarang
import com.bornewtech.marketplacepesaing.ui.pedagang.barang.InputBarang
import com.bornewtech.marketplacepesaing.ui.pedagang.barang.recyclerView.RvListBarang
import com.bornewtech.marketplacepesaing.ui.pembeli.home.DashboardPembeli
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var textName : TextView
    lateinit var textEmail : TextView
    lateinit var btnLogout : Button
    lateinit var btnInputBarang : Button
    lateinit var btnDetailBarang : Button
    lateinit var btnListBarang : Button
    lateinit var btnMaps: Button
    lateinit var dashboardPembeli: Button
    lateinit var btnKeDashboardPedagang: Button

    val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textName = findViewById(R.id.full_name)
        textEmail = findViewById(R.id.email)
        btnLogout = findViewById(R.id.btn_logout)
        btnInputBarang = findViewById(R.id.inp_barang)
        btnDetailBarang = findViewById(R.id.detail_barang)
        btnListBarang = findViewById(R.id.rv_listbarang)
        btnMaps = findViewById(R.id.mapsbtn)
        dashboardPembeli = findViewById(R.id.dashboard_pembeli)
        supportActionBar?.hide()

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            textName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email
        }else{
            startActivity(Intent(this, LoginPedagang::class.java))
            finish()
        }

        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginPedagang::class.java))
            finish()
        }

        btnInputBarang.setOnClickListener {
            startActivity(Intent(this, InputBarang::class.java))
            finish()
        }

        btnDetailBarang.setOnClickListener {
            startActivity(Intent(this, DetailBarang::class.java))
            finish()
        }

        btnListBarang.setOnClickListener {
            startActivity(Intent(this, RvListBarang::class.java))
            finish()
        }
        btnMaps.setOnClickListener {
            startActivity(Intent(this, MapsPesaing::class.java))
            finish()
        }
        dashboardPembeli.setOnClickListener {
            startActivity(Intent(this, DashboardPembeli::class.java))
        }
    }
}