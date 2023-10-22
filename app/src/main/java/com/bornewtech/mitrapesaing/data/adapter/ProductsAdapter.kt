package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.Products

class ProductsAdapter(private val productList: ArrayList<Products>) :
    RecyclerView.Adapter<ProductsAdapter.MyViewHolderProducts>() {
    class MyViewHolderProducts(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.namaBarang)
        val stok: TextView = itemView.findViewById(R.id.stokBarang)
        val harga: TextView = itemView.findViewById(R.id.hargaBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderProducts {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent,false)
        return MyViewHolderProducts(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolderProducts, position: Int) {
        holder.nama.text = productList[position].nama
        holder.stok.text = productList[position].stok.toString()
        holder.harga.text = productList[position].harga.toString()
    }

    override fun getItemCount(): Int {
        return productList.size
    }


}