package com.bornewtech.marketplacepesaing.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityRegisterPembeliBinding

class RegisterPembeli : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPembeliBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pembeli)
    }

    private fun registerFirebase(){
        val name = binding.tvNameusers.text.toString()
        val phone = binding.tvPhone.text.toString()
        val email = binding.tvEmail.text.toString()
        val password = binding.tvPassword.text.toString()
    }
}