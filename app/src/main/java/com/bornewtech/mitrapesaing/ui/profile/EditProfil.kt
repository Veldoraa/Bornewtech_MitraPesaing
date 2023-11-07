package com.bornewtech.mitrapesaing.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.camera.utility.getImageUri
import com.bornewtech.mitrapesaing.databinding.ActivityEditProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfil : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding
    private var dbProfil  = Firebase.firestore
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addImgProfil.setOnClickListener {
            startActivity(Intent(this, AddPhoto::class.java))
        }
        binding.backToProfil.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
        setData()
//        binding.addImgProfil.setOnClickListener { startCamera() }

        binding.btnSimpanProfil.setOnClickListener {
            val nameProfil = binding.inpNamaProfil.text.toString().trim()
            val noHpProfil = binding.inpNoHpProfil.text.toString().trim()
            val alamatProfil = binding.inpAlamatProfil.text.toString().trim()

            val profilMap = hashMapOf(
                "Nama Lengkap" to nameProfil,
                "No Hp/Wa Aktif" to noHpProfil,
                "Alamat Lengkap" to alamatProfil
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbProfil.collection("Profils").document(userId).set(profilMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProfil.text
                    binding.inpNoHpProfil.text
                    binding.inpAlamatProfil.text
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                }
        }

//        currentImageUri?.let { uri ->
//            storageRef.getReference("Gambar Barang")
//                .child(System.currentTimeMillis().toString())
//                .putFile(uri)
//                .addOnSuccessListener {
//                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
//                    val mapImage = mapOf(
//                        "url" to it.toString()
//                    )
//                    val databaseReferences =
//                        FirebaseDatabase.getInstance().getReference("gambarBarang")
//                    databaseReferences.child(userId).setValue(mapImage)
//                        .addOnSuccessListener {
//                            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { error ->
//                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                }
//
//        }

//        // upload gambar dengan Kamera
//        private fun startCamera() {
//            currentImageUri = getImageUri(this)
//            launcherIntentCamera.launch(currentImageUri)
//        }
//        // ngelaunch camera
//        private val launcherIntentCamera = registerForActivityResult(
//            ActivityResultContracts.TakePicture()
//        ) { isSuccess ->
//            if (isSuccess) {
//                showImage()
//            }
//        }
//        // Menampilkan Gambar di Tampilan kotak image
//        private fun showImage() {
//            currentImageUri?.let {
//                Log.d("Image URI", "showImage: $it")
//                binding.imgUser.setImageURI(it)
//            }
//        }
    }
    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Profils").document(userId)
        refProfil.get()
            .addOnSuccessListener {
                if (it != null ){
                    val nama = it.data?.get("Nama Lengkap").toString()
                    val noHp = it.data?.get("No Hp/Wa Aktif").toString()
                    val alamat = it.data?.get("Alamat Lengkap").toString()

                    binding.inpNamaProfil.setText(nama)
                    binding.inpNoHpProfil.setText(noHp)
                    binding.inpAlamatProfil.setText(alamat)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
            }
    }
}