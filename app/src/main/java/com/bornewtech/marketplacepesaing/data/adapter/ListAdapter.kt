package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.model.ListBarang

class ListAdapter(private val barangList: ArrayList<ListBarang>): RecyclerView.Adapter<ListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListAdapter.MyViewHolder, position: Int) {
        val barang: ListBarang = barangList[position]
        holder.stokBrg.text = barang.stok_barang.toString()
        holder.namaBrg.text = barang.nama_barang
        holder.hargaBrg.text = barang.harga_barang.toString()
    }

    override fun getItemCount(): Int {
        return barangList.size
    }

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.img_barangRV)
        val namaBrg : TextView = itemView.findViewById(R.id.tv_namaBarangRV)
        val stokBrg : TextView = itemView.findViewById(R.id.tv_stokRV)
        val hargaBrg : TextView = itemView.findViewById(R.id.tv_hargaBarangRV)
    }
}