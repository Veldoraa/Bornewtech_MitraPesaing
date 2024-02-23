package com.bornewtech.mitrapesaing.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.R
import com.bornewtech.mitrapesaing.data.firestoreDb.Orderan

class AdapterPesanan(private val dataList: MutableList<Orderan>) : RecyclerView.Adapter<AdapterPesanan.ViewHolder>() {

    // Interface untuk menangani klik item
    interface OnItemClickListener {
        fun onItemClick(orderan: Orderan)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaPembeli: TextView = itemView.findViewById(R.id.namaorangTransaksi)
        val statusTransaksi: TextView = itemView.findViewById(R.id.statusTransaksi)
        val hargaProduk: TextView = itemView.findViewById(R.id.hargaProduk)

        init {
            // Set listener untuk item
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                    itemClickListener!!.onItemClick(dataList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.namaPembeli.text = data.namaPembeli
        holder.statusTransaksi.text = data.status
        holder.hargaProduk.text = "Harga total: ${data.totalHarga}"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}