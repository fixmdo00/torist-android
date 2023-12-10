package com.example.torist.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.BuatPesananActivity
import com.example.torist.R
import com.example.torist.data.ProdukKeranjang
import com.example.torist.data.Toko
import com.example.torist.`object`.Keranjang
import java.text.NumberFormat
import java.util.Locale

class KeranjangTokoAdapater (private val productList:ArrayList<Toko>, val lifecycleOwner : LifecycleOwner,  val loadingBar : ProgressBar, val context : Context) : RecyclerView.Adapter<KeranjangTokoAdapater.ProductsViewHolder>() {

    private lateinit var keranjangTokoProdukAdapater: KeranjangTokoProdukAdapater
    private lateinit var recyclerView: RecyclerView
    private var produkList = ArrayList<ProdukKeranjang>()

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang_toko,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        holder.tvNamaToko.text = currentItem.toko_name
        holder.tvAlamatToko.text = currentItem.toko_address

        recyclerView = holder.rv
        recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        recyclerView.setHasFixedSize(true)

        keranjangTokoProdukAdapater = KeranjangTokoProdukAdapater(produkList, loadingBar)
        recyclerView.adapter = keranjangTokoProdukAdapater

        val _totalHarga = getTotalHarga(Keranjang.getProductArrayListPerToko(currentItem.id))
        val totalHarga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(_totalHarga)
        holder.tvTotalHarga.text = totalHarga

        Keranjang.liveKerItems.observe(lifecycleOwner,{
            keranjangTokoProdukAdapater.list = Keranjang.getProductArrayListPerToko(currentItem.id)
            keranjangTokoProdukAdapater.notifyDataSetChanged()
        })

        holder.btnBeli.setOnClickListener {
            val intent = Intent(context, BuatPesananActivity::class.java)
            intent.putExtra("id_toko", currentItem.id.toString())
            context.startActivity(intent)
        }

    }

    private fun getTotalHarga(list : ArrayList<ProdukKeranjang>) : Int {
        val len = list.size
        Log.d("xxx", len.toString())
        var total : Int = 0
        for ( i in 0 until len){
            Log.d("xxx", list[i].toString())
            total += list[i].harga * list[i].jumlah
        }
        return total
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNamaToko : TextView = itemView.findViewById(R.id.keranjangTokoNamaToko)
        val tvAlamatToko : TextView = itemView.findViewById(R.id.keranjangTokoAlamatToko)
        val tvTotalHarga : TextView = itemView.findViewById(R.id.keranjangTokoTotalHarga)
        val btnBeli : Button = itemView.findViewById(R.id.keranjangTokoBtnBeli)
        val rv : RecyclerView = itemView.findViewById(R.id.RVKeranjangPerTokoItem)
    }
}