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
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.BuatPesananActivity
import com.example.torist.DetailPesananActivity
import com.example.torist.R
import com.example.torist.TokoDetailPesananActivity
import com.example.torist.data.Pesanan
import com.example.torist.data.ProdukKeranjang
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.StatusPesanan
import java.text.NumberFormat
import java.util.Locale

class TokoPesananAdapater (private val productList:ArrayList<Pesanan>, val lifecycleOwner : LifecycleOwner, val loadingBar : ProgressBar, val context : Context) : RecyclerView.Adapter<TokoPesananAdapater.ProductsViewHolder>() {

    private var produkList = ArrayList<ProdukKeranjang>()

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_toko_pesanan,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]

        holder.tvWaktu.text = currentItem.order_time
        holder.tvNamaPenerima.text = currentItem.order_name
        holder.tvAlamatPengiriman.text = currentItem.order_address
        holder.tvJumlahProduk.text = currentItem.order_products_qty.toString() + " Produk"
        holder.tvStatus.text = StatusPesanan.getTokoStatus(currentItem.order_status)

        val totalHargaPesanan = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(currentItem.order_total_price + currentItem.order_delivery_price)
        holder.tvTotalHarga.text = totalHargaPesanan
        holder.btnLihatDetail.setOnClickListener {
            val intent = Intent(context, TokoDetailPesananActivity::class.java)
            intent.putExtra("id_pesanan", currentItem.order_id)
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
        val tvWaktu : TextView = itemView.findViewById(R.id.itemTokoPesananWaktu)
        val tvStatus : TextView = itemView.findViewById(R.id.itemTokoPesananStatus)
        val tvNamaPenerima : TextView = itemView.findViewById(R.id.itemTokoPesananNamaPenerima)
        val tvAlamatPengiriman : TextView = itemView.findViewById(R.id.itemTokoPesananAlamatPengiriman)
        val tvJumlahProduk : TextView = itemView.findViewById(R.id.itemTokoPesananJumlahProduk)
        val tvTotalHarga : TextView = itemView.findViewById(R.id.itemTokoPesananTotalHargaPesanan)
        val btnLihatDetail : Button = itemView.findViewById(R.id.itemTokoPesananBtnLihatDetail)
    }
}