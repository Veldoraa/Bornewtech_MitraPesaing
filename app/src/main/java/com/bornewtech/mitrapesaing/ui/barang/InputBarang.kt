package com.bornewtech.mitrapesaing.ui.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityInputBarangBinding

class InputBarang : AppCompatActivity() {
    private lateinit var binding: ActivityInputBarangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}