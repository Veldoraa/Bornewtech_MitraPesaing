package com.bornewtech.marketplacepesaing.ui.register

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bornewtech.marketplacepesaing.databinding.ActivityRegisterPedagangBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class RegisterPedagang : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterPedagangBinding
    private lateinit var progressDialog : ProgressDialog
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPedagangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonRegister = binding.btnRegister
        val buttonLogin = binding.btnLogin

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Silahkan Tunggu")

        buttonLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        buttonRegister.setOnClickListener {
            val sellerName = binding.tvSellername.text.toString().trim()
            val sellerEmail = binding.tvSelleremail.text.toString().trim()
            val sellerPassword = binding.tvSellerpassword.text.toString().trim()
            val sellerPasswordConf = binding.tvConfirmsellerpassword.text.toString().trim()
            if (sellerName.isNotEmpty() && sellerEmail.isNotEmpty() && sellerPassword.isNotEmpty()){
                if (sellerPassword == sellerPasswordConf){
                    prosesRegister()
                }else {
                    Toast.makeText(this, "Konfirmasi Kata Sandi Harus Sama", LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, "Silahkan Isi Dulu Semua Data", LENGTH_SHORT).show()
            }
        }
    }
    private fun prosesRegister(){
        val nameseller = binding.tvSellername.text.toString()
        val email = binding.tvSelleremail.text.toString()
        val password = binding.tvSellerpassword.text.toString()

        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val sellerUpdateProfile = userProfileChangeRequest {
                        displayName = nameseller
                    }
                    val seller = task.result.user
                    seller!!.updateProfile(sellerUpdateProfile)
                        .addOnCompleteListener {
                            progressDialog.dismiss()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener { error2 ->
                            Toast.makeText(this, error2.localizedMessage, LENGTH_SHORT).show()
                        }
                }else{
                    progressDialog.dismiss()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
    }
}