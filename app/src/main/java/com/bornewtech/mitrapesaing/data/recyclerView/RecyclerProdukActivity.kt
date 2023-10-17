package com.bornewtech.mitrapesaing.data.recyclerView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.ProductsAdapter
import com.bornewtech.mitrapesaing.data.firestoreDb.Products
import com.bornewtech.mitrapesaing.databinding.ActivityRecyclerProdukBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecyclerProdukActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerProdukBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var productsList: ArrayList<Products>

    private var dbBarang = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbBarang = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.listbarangRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productsList = arrayListOf()

        dbBarang.collection("Products").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val produk: Products? = data.toObject<Products>(Products::class.java)
                        productsList.add(produk!!)
                    }
                    recyclerView.adapter = ProductsAdapter(productsList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}