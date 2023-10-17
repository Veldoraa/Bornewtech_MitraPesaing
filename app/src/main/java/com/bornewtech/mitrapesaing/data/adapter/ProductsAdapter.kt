package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.Products

class ProductsAdapter(private val productsList:ArrayList<Products> ) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>(){

    class ProductsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val rv_namaBarang:TextView = itemView.findViewById(R.id.namaBarang)
        val rv_stokBarang:TextView = itemView.findViewById(R.id.stokBarang)
        val rv_hargaBarang:TextView = itemView.findViewById(R.id.hargaBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.rv_namaBarang.text = productsList[position].namaBarang
        holder.rv_stokBarang.text = productsList[position].stokBarang.toString()
        holder.rv_hargaBarang.text = productsList[position].hargaBarang.toString()
    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}