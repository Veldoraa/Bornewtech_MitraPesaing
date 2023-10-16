package com.bornewtech.mitrapesaing.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityProfilBinding

class Profil : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}