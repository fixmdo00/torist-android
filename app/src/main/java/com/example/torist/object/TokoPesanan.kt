package com.example.torist.`object`

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.data.Pesanan
import com.example.torist.data.ProdukDetailPesanan
import com.example.torist.data.ProdukKeranjang
import org.json.JSONArray
import org.json.JSONException

object TokoPesanan {

    var _pesanan = JSONArray()
    private val _livePesanan = MutableLiveData<JSONArray>()
    val livePesanan : LiveData<JSONArray> = _livePesanan

    var _products = JSONArray()
    private val _liveProducts = MutableLiveData<JSONArray>()
    val liveProducts : LiveData<JSONArray> = _liveProducts

    fun getFromDB(progressBar: ProgressBar){
        progressBar.visibility = View.VISIBLE
        var toko_id = LoggedInUser.idToko
        val url = "https://torist.my.id/api/get_shop_orders.php?shop_id=$toko_id"
        Log.d("pzn", url)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _pesanan = response
                    _livePesanan.postValue(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    fun getFromDB(progressBar: ProgressBar, callback : (Boolean) -> Unit){
        progressBar.visibility = View.VISIBLE
        var toko_id = LoggedInUser.idToko
        val url = "https://torist.my.id/api/get_shop_orders.php?shop_id=$toko_id"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _pesanan = response
                    _livePesanan.postValue(response)
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                Log.d("err",error.toString())
                callback(false)
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    fun getProductListFromDB(order_id : String, progressBar: ProgressBar, callback : (Boolean) -> Unit){
        progressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/get_order_products.php?order_id=$order_id"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _products = response
                    _liveProducts.postValue(response)
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                progressBar.visibility = View.GONE
                Log.d("err",error.toString())
                callback(false)
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    fun getPesananArrayList() : ArrayList<Pesanan> {
        var list = ArrayList<Pesanan>()
        try {
            for( i in 0 until _pesanan.length()){
                val item = _pesanan.getJSONObject(i)
                list.add(
                    Pesanan(
                    item.getString("order_id"),
                    item.getString("order_time"),
                    item.getString("order_status"),
                    item.getString("order_payment_type"),
                    item.getString("order_payment_photo"),
                    item.getString("order_name"),
                    item.getString("order_telp"),
                    item.getString("order_address"),
                    item.getString("order_note"),
                    item.getString("jumlah_produk").toInt(),
                    item.getString("nama_toko"),
                    item.getString("alamat_toko"),
                    item.getString("total_harga_pesanan").toInt(),
                    item.getString("order_delivery_price").toInt(),
                    item.getString("order_courier"),
                    item.getString("order_resi"),
                    item.getString("shop_bank"),
                    item.getString("shop_bank_account")
                    )
                )
            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan1 catch", e.toString())
        }
        return list
    }

    fun getPesananDetail(order_id : String) : Pesanan {
        lateinit var list : Pesanan
        try {
            for( i in 0 until _pesanan.length()){
                val item = _pesanan.getJSONObject(i)
                if (item.getString("order_id") == order_id){
                    list = Pesanan(
                        item.getString("order_id"),
                        item.getString("order_time"),
                        item.getString("order_status"),
                        item.getString("order_payment_type"),
                        item.getString("order_payment_photo"),
                        item.getString("order_name"),
                        item.getString("order_telp"),
                        item.getString("order_address"),
                        item.getString("order_note"),
                        item.getString("jumlah_produk").toInt(),
                        item.getString("nama_toko"),
                        item.getString("alamat_toko"),
                        item.getString("total_harga_pesanan").toInt(),
                        item.getString("order_delivery_price").toInt(),
                        item.getString("order_courier"),
                        item.getString("order_resi"),
                        item.getString("shop_bank"),
                        item.getString("shop_bank_account")
                    )
                }
            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan2 catch", e.toString())
        }
        return list
    }

    fun getProductsArrayList() : ArrayList<ProdukDetailPesanan> {
        var list = ArrayList<ProdukDetailPesanan>()
        try {
            for( i in 0 until _products.length()){
                val item = _products.getJSONObject(i)
                list.add(
                    ProdukDetailPesanan(
                        item.getString("op_id"),
                        item.getString("product_id"),
                        item.getString("product_name"),
                        item.getString("product_price").toInt(),
                        item.getString("op_quantity").toInt(),
                        item.getString("product_photo")
                    )
                )
            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan produk catch", e.toString())
        }
        return list
    }

    fun getTotalHarga() : Int{
        var totalHarga = 0
        try {
            for( i in 0 until _products.length()){
                val item = _products.getJSONObject(i)
                val jumlah = item.getString("product_price").toInt() * item.getString("op_quantity").toInt()
                totalHarga += jumlah
            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan produk catch", e.toString())
        }
        return totalHarga
    }
}