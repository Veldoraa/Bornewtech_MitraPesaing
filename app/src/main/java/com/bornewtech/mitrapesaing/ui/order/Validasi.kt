package com.bornewtech.mitrapesaing.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.bornewtech.mitrapesaing.databinding.ActivityValidasiBinding

class Validasi : AppCompatActivity() {
    private lateinit var binding : ActivityValidasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonTerima.setOnClickListener {
            // Menampilkan notifikasi bahwa pesanan diterima
            // Di sini Anda bisa menggunakan library notifikasi atau menampilkan Toast
            // Misalnya:
             Toast.makeText(this, "Kamu menerima pesanan", Toast.LENGTH_SHORT).show()
        }

        binding.buttonTolak.setOnClickListener {
            // Menampilkan notifikasi bahwa pesanan ditolak
            // Di sini Anda bisa menggunakan library notifikasi atau menampilkan Toast
            // Misalnya:
             Toast.makeText(this, "Kamu menolak pesanan", Toast.LENGTH_SHORT).show()
        }
    }
}