package com.bornewtech.mitrapesaing.ui.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan
import com.google.firebase.database.*

class Pesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterPesanan
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dataList: MutableList<Orderan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan)

        recyclerView = findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dataList = mutableListOf()
        adapter = AdapterPesanan(dataList)
        recyclerView.adapter = adapter

        // Menghubungkan dengan Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("riwayatTransaksi")

        // Mendapatkan data dari Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dataList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val data = dataSnapshot.getValue(Orderan::class.java)
                        data?.let {
                            dataList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Menangani error
            }
        })

        // Set listener untuk item RecyclerView
        adapter.setOnItemClickListener(object : AdapterPesanan.OnItemClickListener {
            override fun onItemClick(orderan: Orderan) {
                // Mengarahkan ke DetailPesananActivity dengan membawa data Orderan yang diklik
                val intent = Intent(this@Pesanan, DetailPesanan::class.java)
                intent.putExtra("detailPesanan", orderan)
                startActivity(intent)
            }
        })
    }
}