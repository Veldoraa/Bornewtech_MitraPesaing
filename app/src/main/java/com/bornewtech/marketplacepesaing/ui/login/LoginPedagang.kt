package com.bornewtech.marketplacepesaing.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityLoginPedagangBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.register.RegisterPedagang
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginPedagang : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPedagangBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    private var firebaseAuth = FirebaseAuth.getInstance()

    // request code
    companion object {
        private const val RC_SIGN_IN = 1001
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPedagangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val buttonLogin = binding.btnLogin
        val buttonRegister = binding.btnRegister
        val buttonGoogle = binding.btnGoogle

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Silahkan Tunggu")

        //Signing options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // button login
        buttonLogin.setOnClickListener {
            val sellerEmail = binding.tvSelleremail.text.toString().trim()
            val sellerPassword = binding.tvSellerpassword.text.toString().trim()
            if (sellerEmail.isNotEmpty() && sellerPassword.isNotEmpty()){
                prosesLogin()
            }else{
                Toast.makeText(this, "Silahkan Isi Email dan Password Terlebih Dahulu", LENGTH_SHORT).show()
            }
        }
        buttonGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        // button regis dan diarahkan ke registrasi
        buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterPedagang::class.java))
        }
    }
    private fun prosesLogin(){
        val email = binding.tvSelleremail.text.toString()
        val password = binding.tvSellerpassword.text.toString()

        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            //Menangani Proses Login GOOGLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // jika berhasil
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken : String){
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
    }
}