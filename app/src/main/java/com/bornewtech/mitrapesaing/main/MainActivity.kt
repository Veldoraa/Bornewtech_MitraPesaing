package com.bornewtech.mitrapesaing.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityMainBinding
import com.bornewtech.mitrapesaing.maps.Maps
import com.bornewtech.mitrapesaing.ui.barang.DetailBarang
import com.bornewtech.mitrapesaing.ui.barang.recyclerview.RecViewBarang
import com.bornewtech.mitrapesaing.ui.fragment.home.HomeFragment
import com.bornewtech.mitrapesaing.ui.order.Pesanan
import com.bornewtech.mitrapesaing.ui.profile.Profil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.navigationView.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.home_pedagang -> {
                    //untuk mengarahkan ke fragment ketika bottom nav ditekan
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment).commit()
                }

                R.id.barang_pedagang -> {
                    val intent = Intent(this, RecViewBarang::class.java)
                    startActivity(intent)
                }

                R.id.maps_pedagang -> {
                    val intent = Intent(this, Maps::class.java)
                    startActivity(intent)
                }

                R.id.pesanan_pedagang -> {
                    val intent = Intent(this, Pesanan::class.java)
                    startActivity(intent)
                }

                R.id.akun_pedagang -> {
                    // untuk mengarahkan ke activity ketika bottom nav ditekan
                    val intent = Intent(this, Profil::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}