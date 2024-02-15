package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem

class AdapterDetailPesanan(private val dataList: List<CartItem>) : RecyclerView.Adapter<AdapterDetailPesanan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaProdukPesanan: TextView = itemView.findViewById(R.id.namaProdukPesanan)
        val hargaProdukPesanan: TextView = itemView.findViewById(R.id.hargaProdukPesanan)
        val kuantitiProdukPesanan: TextView = itemView.findViewById(R.id.kuantitiProdukPesanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_detail_pesanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.namaProdukPesanan.text = data.productName
        holder.hargaProdukPesanan.text = "Harga: ${data.productPrice}"
        holder.kuantitiProdukPesanan.text = "Kuantitas: ${data.productQuantity}"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}