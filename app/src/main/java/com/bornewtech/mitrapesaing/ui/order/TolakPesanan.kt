package com.bornewtech.mitrapesaing.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button
import android.widget.Toast
import com.bornewtech.mitrapesaing.R
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class TolakPesanan : AppCompatActivity() {

    private val TAG = "TolakPesanan"

    private lateinit var firestore: FirebaseFirestore
    private lateinit var pembeliId: String
    private lateinit var idTransaksi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tolak_pesanan)
        supportActionBar?.hide()

        firestore = FirebaseFirestore.getInstance()

        pembeliId = intent.getStringExtra("pembeliId") ?: ""
        idTransaksi = intent.getStringExtra("idTransaksi") ?: "" // Pastikan ini ada

        Log.d(TAG, "Data diterima: pembeliId=$pembeliId, idTransaksi=$idTransaksi")

        val spinnerAlasan = findViewById<Spinner>(R.id.spinnerAlasan)

        val alasanMenolak = arrayOf("Karena Jauh", "Karena Menipu", "Karena Stok Habis")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, alasanMenolak)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAlasan.adapter = adapter

        val buttonSelesai = findViewById<Button>(R.id.buttonSelesai)

        buttonSelesai.setOnClickListener {
            val selectedAlasan = spinnerAlasan.selectedItem.toString()
            Toast.makeText(this, "Menolak pesanan karena: $selectedAlasan", Toast.LENGTH_SHORT).show()

            if (selectedAlasan == "Karena Menipu") {
                recordFraudReason() // Catat alasan
            }
        }
    }

    private fun recordFraudReason() {
        Log.d(TAG, "Mencatat penolakan dengan pembeliId=$pembeliId dan idTransaksi=$idTransaksi")

        val data = hashMapOf(
            "reason" to "Karena Menipu",
            "timestamp" to System.currentTimeMillis(),
            "pembeliId" to pembeliId,
            "idTransaksi" to idTransaksi
        )

        firestore.collection("TolakPesanan")
            .document(pembeliId) // Menggunakan pembeliId sebagai document ID
            .set(data) // Menggunakan set() untuk menggantikan/memperbarui data
            .addOnSuccessListener {
                Log.d(TAG, "Data berhasil disimpan dengan ID: $pembeliId")
                Toast.makeText(this, "Data berhasil disimpan dengan ID: $pembeliId", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Gagal menyimpan data: ${exception.message}", exception)
                Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


//package com.bornewtech.mitrapesaing.ui.order
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Spinner
//import android.widget.Button
//import android.widget.Toast
//import com.bornewtech.mitrapesaing.R
//import com.google.firebase.firestore.FirebaseFirestore
//import android.util.Log
//
//class TolakPesanan : AppCompatActivity() {
//    private val TAG = "TolakPesanan"
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var pembeliId: String
//    private lateinit var idTransaksi: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tolak_pesanan)
//        supportActionBar?.hide()
//
//        firestore = FirebaseFirestore.getInstance()
//
//        pembeliId = intent.getStringExtra("pembeliId") ?: ""
//        idTransaksi = intent.getStringExtra("idTransaksi") ?: "" // Pastikan ini ada
//
//        Log.d(TAG, "Data diterima: pembeliId=$pembeliId, idTransaksi=$idTransaksi")
//
//        val spinnerAlasan = findViewById<Spinner>(R.id.spinnerAlasan)
//
//        val alasanMenolak = arrayOf("Karena Jauh", "Karena Menipu", "Karena Stok Habis")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, alasanMenolak)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerAlasan.adapter = adapter
//
//        val buttonSelesai = findViewById<Button>(R.id.buttonSelesai)
//
//        buttonSelesai.setOnClickListener {
//            val selectedAlasan = spinnerAlasan.selectedItem.toString()
//            Toast.makeText(this, "Menolak pesanan karena: $selectedAlasan", Toast.LENGTH_SHORT).show()
//
//            if (selectedAlasan == "Karena Menipu") {
//                recordFraudReason() // Catat alasan
//            }
//        }
//    }
//
//    private fun recordFraudReason() {
//        Log.d(TAG, "Mencatat penolakan dengan pembeliId=$pembeliId dan idTransaksi=$idTransaksi")
//
//        val data = hashMapOf(
//            "reason" to "Karena Menipu",
//            "timestamp" to System.currentTimeMillis(),
//            "pembeliId" to pembeliId,
//            "idTransaksi" to idTransaksi
//        )
//
//        firestore.collection("TolakPesanan")
//            .add(data)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "Data berhasil disimpan dengan ID: ${documentReference.id}")
//                Toast.makeText(this, "Data berhasil disimpan dengan ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { exception ->
//                Log.e(TAG, "Gagal menyimpan data: ${exception.message}", exception)
//                Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}





//package com.bornewtech.mitrapesaing.ui.order
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.Spinner
//import android.widget.Toast
//import com.bornewtech.mitrapesaing.R
//import com.google.firebase.firestore.FirebaseFirestore
//
//class TolakPesanan : AppCompatActivity() {
//    private lateinit var firestore: FirebaseFirestore // Firebase Firestore instance
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tolak_pesanan)
//        supportActionBar?.hide()
//
//        firestore = FirebaseFirestore.getInstance() // Initialize Firestore instance
//
//        val spinnerAlasan: Spinner = findViewById(R.id.spinnerAlasan)
//
//        val alasanMenolak = arrayOf("Karena Jauh", "Karena Menipu", "Karena Stok Habis")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, alasanMenolak)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerAlasan.adapter = adapter
//
//        val buttonSelesai: Button = findViewById(R.id.buttonSelesai)
//
//        buttonSelesai.setOnClickListener {
//            val selectedAlasan = spinnerAlasan.selectedItem.toString() // Get the selected item from Spinner
//            Toast.makeText(this, "Menolak pesanan karena: $selectedAlasan", Toast.LENGTH_SHORT).show()
//
//            if (selectedAlasan == "Karena Menipu") {
//                // Call method to add information to Firestore when "Karena Menipu" is selected
//                recordFraudReason() // Assuming this is a predefined method
//            }
//        }
//    }
//
//    // Method to record the reason in Firestore
//    private fun recordFraudReason() {
//        // Create a document with a unique identifier and set data
//        val data = hashMapOf("reason" to "Karena Menipu", "timestamp" to System.currentTimeMillis())
//        firestore.collection("RejectedOrders") // Firestore collection for rejected orders
//            .add(data)
//            .addOnSuccessListener { documentReference ->
//                Toast.makeText(this, "Data successfully recorded with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Failed to record data: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
