package com.example.torist.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.torist.R
import com.example.torist.data.ProdukKeranjang
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class BuatPesananProdukAdapater (private val productList:ArrayList<ProdukKeranjang>, val loadingBar : ProgressBar) : RecyclerView.Adapter<BuatPesananProdukAdapater.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_buat_pesanan_produk,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]
        val _totalHarga = currentItem.harga * currentItem.jumlah
        val totalHarga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(_totalHarga)

        val harga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.harga)

        holder.tvNamaProduk.text = currentItem.nama
        holder.tvHargaProduk.text = harga
        holder.tvQuantityProduk.text = currentItem.jumlah.toString() + " pcs"
        holder.tvTotalHarga.text = totalHarga
        Picasso.get()
            .load("https://torist.my.id" + currentItem.foto)
            .placeholder(R.drawable.loading_background)
            .into(holder.produkImage)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNamaProduk : TextView = itemView.findViewById(R.id.buatPesananNama)
        val tvHargaProduk : TextView = itemView.findViewById(R.id.buatPesananHarga)
        val tvTotalHarga : TextView = itemView.findViewById(R.id.buatPesananTotalHarga)
        val tvQuantityProduk : TextView = itemView.findViewById(R.id.buatPesananJumlah)
        val produkImage : ImageView = itemView.findViewById(R.id.buatPesananImage)
    }


}