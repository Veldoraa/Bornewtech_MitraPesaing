package com.bornewtech.marketplacepesaing.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.databinding.ActivityRegisterPembeliBinding

class RegisterPembeli : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPembeliBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPembeliBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}