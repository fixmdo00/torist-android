package com.example.torist.adapter

import android.text.Editable
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_NULL
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.example.torist.R
import com.example.torist.data.Produk
import com.example.torist.data.ProdukKeranjang
import com.example.torist.data.Toko
import com.example.torist.data.TokoProduct
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class KeranjangTokoProdukAdapater (private val productList:ArrayList<ProdukKeranjang>, val loadingBar : ProgressBar) : RecyclerView.Adapter<KeranjangTokoProdukAdapater.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = productList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang_product,parent,false)
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
        holder.tvQuantityProduk.setText(currentItem.jumlah.toString())
        holder.tvTotalHarga.text = totalHarga
        Picasso.get()
            .load("https://torist.my.id" + currentItem.foto)
            .placeholder(R.drawable.loading_background_2)
            .into(holder.produkImage)
        holder.btnMinus.setOnClickListener {
            changeQuantity(currentItem.id,currentItem.jumlah,"minus", 0 )
        }
        holder.btnPlus.setOnClickListener {
            changeQuantity(currentItem.id,currentItem.jumlah, "plus", 0)
        }

        holder.tvQuantityProduk.setOnEditorActionListener { _, actionId, _ ->
            Log.d("pzn done1",actionId.toString())
            Log.d("pzn done2",holder.tvQuantityProduk.text.toString())
            holder.tvQuantityProduk.clearFocus()
            if (holder.tvQuantityProduk.text.toString().toInt() == 0){
                changeQuantity(currentItem.id,1, "minus", holder.tvQuantityProduk.text.toString().toInt())
            } else {
                changeQuantity(currentItem.id,currentItem.jumlah, "plus", holder.tvQuantityProduk.text.toString().toInt())
            }
            return@setOnEditorActionListener false
        }
    }

    private fun changeQuantity(id: String,jumlah : Int, action: String, jumlahBaru: Int) {
        loadingBar.visibility = View.VISIBLE
        Log.d("pzn add keranjang item id",id)
        val params = JSONObject()
        val url = "https://torist.my.id/api/change_cart_item.php"
        params.put("action",action)
        params.put("cart_id",id)
        params.put("jumlah",jumlah)
        params.put("jumlah_baru",jumlahBaru)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                val status = response.getString("status")
                Log.d("pzn add keranjang item",status)
                Log.d("pzn add keranjang item",params.toString())
                Keranjang.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
            },
            { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Log.d("pzn add keranjang item err",params.toString())
            })
        RQ.getRQ().add(jsonObjectRequest)
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNamaProduk : TextView = itemView.findViewById(R.id.keranjangProdukNama)
        val tvHargaProduk : TextView = itemView.findViewById(R.id.keranjangProdukHarga)
        val tvTotalHarga : TextView = itemView.findViewById(R.id.keranjangProdukTotalHarga)
        val tvQuantityProduk : EditText = itemView.findViewById(R.id.keranjangProdukQuantity)
        val btnPlus : Button = itemView.findViewById(R.id.keranjangProdukBtnPlus)
        val btnMinus : Button = itemView.findViewById(R.id.keranjangProduktMinus)
        val produkImage : ImageView = itemView.findViewById(R.id.keranjangProdukImageView)
    }


}