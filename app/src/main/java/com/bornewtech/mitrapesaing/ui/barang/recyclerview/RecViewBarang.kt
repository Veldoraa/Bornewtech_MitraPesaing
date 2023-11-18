package com.bornewtech.mitrapesaing.ui.barang.recyclerview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterProduct
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityRecViewBarangBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class RecViewBarang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewBarangBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<Products>
    private lateinit var adapterProduk: AdapterProduct
    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecViewBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        productList = arrayListOf()

        adapterProduk = AdapterProduct(productList)
        recyclerView.adapter = adapterProduk

        EventChangeListener()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun EventChangeListener() {
        dbBarang = FirebaseFirestore.getInstance()
        dbBarang.collection("Products")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }
                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED) {
                            productList.add(dc.document.toObject(Products::class.java))
                        }
                    }
                    adapterProduk.notifyDataSetChanged()
                }
            })
    }
}