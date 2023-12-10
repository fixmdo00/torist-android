package com.example.torist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.R
import com.example.torist.data.Produk
import com.example.torist.data.TokoProduct
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ProdukAdapater (private val productList:ArrayList<Produk>) : RecyclerView.Adapter<ProdukAdapater.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_all_product,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        val harga = NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.harga)

        holder.tvNama.text = currentItem.nama
        holder.tvHarga.text = "Rp. $harga"
        holder.tvShop.text = currentItem.shop_name
        Picasso.get()
            .load("https://torist.my.id" + currentItem.foto)
            .placeholder(R.drawable.loading_background_2)
            .into(holder.ivFoto)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(
                arrayOf(currentItem.nama,currentItem.id)
            )
        }
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNama : TextView = itemView.findViewById(R.id.itemProductNama)
        val tvHarga : TextView = itemView.findViewById(R.id.itemProductHarga)
        val tvShop : TextView = itemView.findViewById(R.id.itemProdukShop)
        val ivFoto : ImageView = itemView.findViewById(R.id.itemProductImageView)
    }
}