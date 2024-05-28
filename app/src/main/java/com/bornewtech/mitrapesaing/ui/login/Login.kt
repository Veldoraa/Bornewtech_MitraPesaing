package com.bornewtech.mitrapesaing.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.databinding.ActivityLoginBinding
import com.bornewtech.mitrapesaing.main.MainActivity
import com.bornewtech.mitrapesaing.ui.register.Registrasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth

        // Tombol ke registrasi
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, Registrasi::class.java))
        }
        // Tombol untuk login
        binding.btnLogin.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            checkSuspension(user.uid)
                        }
                    } else {
                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkSuspension(pedagangId: String) {
        db.collection("pelaporan")
            .whereEqualTo("idPedagang", pedagangId)
            .get()
            .addOnSuccessListener { documents ->
                var isSuspended = false
                for (document in documents) {
                    val suspendedUntil = document.getLong("timestamp")
                    if (suspendedUntil != null && suspendedUntil > System.currentTimeMillis()) {
                        isSuspended = true
                        val remainingTime = suspendedUntil - System.currentTimeMillis()
                        val remainingTimeString = formatRemainingTime(remainingTime)
                        Toast.makeText(this, "Akun Anda ditangguhkan selama $remainingTimeString", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        break
                    }
                }
                if (!isSuspended) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memeriksa status penangguhan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatRemainingTime(remainingTime: Long): String {
        val seconds = remainingTime / 1000 % 60
        val minutes = remainingTime / (1000 * 60) % 60
        val hours = remainingTime / (1000 * 60 * 60) % 24
        val days = remainingTime / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "$days hari"
            hours > 0 -> "$hours jam"
            minutes > 0 -> "$minutes menit"
            else -> "$seconds detik"
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                binding.tilemail.error = "Masukkan Email"
                false
            }

            TextUtils.isEmpty(password) -> {
                binding.tilpassword.error = "Masukkan Password"
                false
            }

            else -> {
                true
            }
        }
    }
}




//package com.bornewtech.mitrapesaing.ui.login
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.databinding.ActivityLoginBinding
//import com.bornewtech.mitrapesaing.main.MainActivity
//import com.bornewtech.mitrapesaing.ui.register.Registrasi
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthCredential
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth:FirebaseAuth
//    private var firebaseAuth = FirebaseAuth.getInstance()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        auth = Firebase.auth
//
//        // tombol ke registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//        //tombol untuk login
//        binding.btnLogin.setOnClickListener {loginUser()}
//    }
//
//    private fun loginUser(){
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//        if (validateForm(email, password)){
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        startActivity(Intent(this, MainActivity::class.java))
//                    } else {
//                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//    private fun validateForm(email: String, password: String): Boolean {
//        return when {
//            TextUtils.isEmpty(email) -> {
//                binding.tilemail.error = "Masukkan Email"
//                false
//            }
//
//            TextUtils.isEmpty(password) -> {
//                binding.tilpassword.error = "Masukkan Password"
//                false
//            }
//
//            else -> {
//                true
//            }
//        }
//    }
//}

//package com.bornewtech.mitrapesaing.ui.login
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import com.bornewtech.mitrapesaing.R
//import com.bornewtech.mitrapesaing.databinding.ActivityLoginBinding
//import com.bornewtech.mitrapesaing.main.MainActivity
//import com.bornewtech.mitrapesaing.ui.register.Registrasi
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthCredential
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import java.util.Date
//
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth: FirebaseAuth
//    private var db = Firebase.firestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        auth = Firebase.auth
//        // tombol ke registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//        // tombol untuk login
//        binding.btnLogin.setOnClickListener { loginUser() }
//    }
//
//    private fun loginUser() {
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//        if (validateForm(email, password)) {
//            checkIfAccountIsSuspended(email, password)
//        }
//    }
//
//    private fun checkIfAccountIsSuspended(email: String, password: String) {
//        db.collection("users").whereEqualTo("email", email).get()
//            .addOnSuccessListener { documents ->
//                if (documents != null && !documents.isEmpty) {
//                    val userId = documents.documents[0].id
//                    val suspensionRef = db.collection("suspensions").document(userId)
//
//                    suspensionRef.get()
//                        .addOnSuccessListener { document ->
//                            if (document != null && document.exists()) {
//                                val suspensionEnd = document.getLong("suspensionEnd")
//                                if (suspensionEnd != null && suspensionEnd > System.currentTimeMillis()) {
//                                    Toast.makeText(this, "Akun Anda ditangguhkan hingga ${Date(suspensionEnd)}", Toast.LENGTH_LONG).show()
//                                } else {
//                                    loginWithEmailAndPassword(email, password)
//                                }
//                            } else {
//                                loginWithEmailAndPassword(email, password)
//                            }
//                        }
//                } else {
//                    Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun loginWithEmailAndPassword(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    startActivity(Intent(this, MainActivity::class.java))
//                } else {
//                    Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun validateForm(email: String, password: String): Boolean {
//        return when {
//            TextUtils.isEmpty(email) -> {
//                binding.tilemail.error = "Masukkan Email"
//                false
//            }
//            TextUtils.isEmpty(password) -> {
//                binding.tilpassword.error = "Masukkan Password"
//                false
//            }
//            else -> {
//                true
//            }
//        }
//    }
//}
