package com.bornewtech.mitrapesaing.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.mitrapesaing.data.firestoreDb.CartItem
import com.bornewtech.mitrapesaing.data.firestoreDb.Transaction
import com.bornewtech.mitrapesaing.databinding.ListPesananBinding

class AdapterPesanan(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<AdapterPesanan.ViewHolder>() {

    class ViewHolder(private val binding: ListPesananBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.namaBarangTransaksi.text = "Transaction ID: ${transaction.idTransaksi}"
            binding.statusTransaksi.text = "Total Harga: Rp ${transaction.jumlahHarga},00"

            // Example: Display the first cart item's information
            val firstCartItem = transaction.cartItems?.firstOrNull()
            if (firstCartItem != null) {
                binding.namaBarangTransaksi.text = "Product Name: ${firstCartItem.productName}"
                binding.statusTransaksi.text = "Product Price: Rp ${firstCartItem.productPrice},00"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTransaction = transactions[position]
        holder.bind(currentTransaction)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
