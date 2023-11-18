package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.Products

class AdapterProduct(private val productList: ArrayList<Products>) :
    RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {
    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val stokBarang: TextView = itemView.findViewById(R.id.stokBarang)
        val hargaBarang: TextView = itemView.findViewById(R.id.hargaBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val produk: Products = productList[position]
        holder.namaBarang.text = produk.nama.toString()
        holder.stokBarang.text = produk.stok.toString()
        holder.hargaBarang.text = produk.harga.toString()
    }
}