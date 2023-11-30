package com.bornewtech.mitrapesaing.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityProfilBinding
import com.bornewtech.mitrapesaing.main.MainActivity
import com.bornewtech.mitrapesaing.ui.login.Login
import com.bornewtech.mitrapesaing.ui.login.PraLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Profil : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dbProfil = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setData()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.editProfil.setOnClickListener {
            startActivity(Intent(this, EditProfil::class.java))
        }
        binding.backToDP.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGantiPassword.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
            finish()
        }

        binding.btnLogoutPdg.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, PraLogin::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Profils").document(userId)
        refProfil.get()
            .addOnSuccessListener {
                if (it != null) {
                    val nama = it.data?.get("namaLengkap").toString()
                    val noHp = it.data?.get("noHpAktif").toString()

                    binding.namaPengguna.setText(nama)
                    binding.noPengguna.setText(noHp)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
            }
    }
}