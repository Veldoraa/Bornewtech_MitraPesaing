package com.bornewtech.mitrapesaing.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityRegistrasiBinding

class Registrasi : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}