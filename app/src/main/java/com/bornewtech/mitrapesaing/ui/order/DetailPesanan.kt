package com.bornewtech.mitrapesaing.ui.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.adapter.AdapterDetailPesanan
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan

class DetailPesanan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterDetailPesanan
    private lateinit var dataList: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)

        recyclerView = findViewById(R.id.rvCart)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val orderan = intent.getSerializableExtra("detailPesanan") as? Orderan
        orderan?.let {
            dataList = it.cartItems ?: emptyList()
            adapter = AdapterDetailPesanan(dataList)
            recyclerView.adapter = adapter
        }
    }
}
