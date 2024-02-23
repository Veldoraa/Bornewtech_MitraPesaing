package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem

class AdapterDetailPesanan : RecyclerView.Adapter<AdapterDetailPesanan.ViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.namaProdukPesanan)
        val productPrice: TextView = itemView.findViewById(R.id.hargaProdukPesanan)
        val productQuantity: TextView = itemView.findViewById(R.id.kuantitiProdukPesanan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_detail_pesanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]
        holder.productName.text = item.productName
        holder.productPrice.text = "Harga: ${item.productPrice}"
        holder.productQuantity.text = "Kuantitas: ${item.productQuantity}"
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun setData(data: List<CartItem>) {
        cartItems = data
        notifyDataSetChanged()
    }
}
