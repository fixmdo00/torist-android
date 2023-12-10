package com.example.torist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.R
import com.example.torist.data.TokoProduct
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class TokoProdukAdapater (private val productList:ArrayList<TokoProduct>) : RecyclerView.Adapter<TokoProdukAdapater.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_toko_product,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        val harga = NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.harga)
        var desk: String
        if (currentItem.desk.length > 30){
            desk = currentItem.desk.substring(0,30)
        } else {
            desk = currentItem.desk
        }

        holder.tvNama.text = currentItem.nama
        holder.tvHarga.text = "Rp. $harga"
        holder.tvDesk.text = desk
        Picasso.get()
            .load("https://torist.my.id" + currentItem.foto)
            .placeholder(R.drawable.bunga)
            .into(holder.ivFoto)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNama : TextView = itemView.findViewById(R.id.odpiNama)
        val tvHarga : TextView = itemView.findViewById(R.id.odpiHarga)
        val tvDesk : TextView = itemView.findViewById(R.id.odpiDesk)
        val ivFoto : ImageView = itemView.findViewById(R.id.odpiImageView)
    }
}