package com.bornewtech.mitrapesaing.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityProfilBinding
import com.bornewtech.mitrapesaing.main.MainActivity
import com.bornewtech.mitrapesaing.ui.login.Login
import com.bornewtech.mitrapesaing.ui.login.PraLogin
import com.google.firebase.auth.FirebaseAuth

class Profil : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.backToDP.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGantiPassword.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
            finish()
        }

        binding.btnLogoutPdg.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, PraLogin::class.java)
            startActivity(intent)
            finish()
        }
    }
}