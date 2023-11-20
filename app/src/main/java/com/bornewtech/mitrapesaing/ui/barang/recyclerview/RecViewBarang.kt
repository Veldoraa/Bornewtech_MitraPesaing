package com.bornewtech.mitrapesaing.ui.barang.recyclerview

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterProduct
import com.bornewtech.mitrapesaing.data.firestoreDb.ProductItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityRecViewBarangBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class RecViewBarang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewBarangBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: List<ProductItem>
    private lateinit var adapterProduk: AdapterProduct
    private val dbBarang = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecViewBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recViewBarang)
        recyclerView.setHasFixedSize(true)

        productList = emptyList()
        adapterProduk = AdapterProduct(productList)
        recyclerView.adapter = adapterProduk

        setupRecyclerView()
        eventChangeListener()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun eventChangeListener() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            dbBarang.collection("Products").document(currentUser.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val productList = documentSnapshot.toObject(Products::class.java)
                        if (productList != null) {
                            // Access the productList directly
                            val productListData = productList.productList
                            // Update RecyclerView data and refresh
                            productListData?.let {
                                adapterProduk.updateData(it)
                                adapterProduk.notifyDataSetChanged()
                            }
                        } else {
                            Log.d(TAG, "Failed to convert document to Products")
                        }
                    } else {
                        Log.d(TAG, "Document does not exist")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting document: ", exception)
                }
        } else {
            Log.d(TAG, "User not authenticated")
        }
    }


}