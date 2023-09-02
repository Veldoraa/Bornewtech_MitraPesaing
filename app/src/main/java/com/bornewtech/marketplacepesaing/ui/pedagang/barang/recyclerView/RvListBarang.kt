package com.bornewtech.marketplacepesaing.ui.pedagang.barang.recyclerView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.ListAdapter
import com.bornewtech.marketplacepesaing.data.model.ListBarang
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class RvListBarang : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var barangArrayList: ArrayList<ListBarang>
    private  var dbBarang = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_list_barang)

        recyclerView = findViewById(R.id.listbarangRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        barangArrayList = arrayListOf()

        dbBarang = FirebaseFirestore.getInstance()
        dbBarang.collection("products").get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (data in it.documents){
                        val barang: ListBarang? = data.toObject(ListBarang::class.java)
                        if (barang != null) {
                            barangArrayList.add(barang)
                        }
                    }
                    recyclerView.adapter = ListAdapter(barangArrayList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

    }
}