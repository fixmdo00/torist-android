package com.example.torist.`object`

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.torist.data.Produk
import com.example.torist.data.ProdukKeranjang
import com.example.torist.data.Toko
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Keranjang {

    var _keranjangItems = JSONArray()

    private val _liveKeranjangItems = MutableLiveData<JSONArray>().apply {
        _keranjangItems
    }

    val liveKerItems: LiveData<JSONArray> = _liveKeranjangItems

    fun getItemsFromDB(user_id : String) {
        val requestQueue: RequestQueue = RQ.getRQ()
        val url = "https://torist.my.id/api/get_cart_items.php?user_id=$user_id"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    _keranjangItems = response
                    Log.d("pzn keranjang",response.toString())
                    _liveKeranjangItems.postValue(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun getItemsFromDB(user_id : String, callback: (Boolean) -> Unit) {
        val requestQueue: RequestQueue = RQ.getRQ()
        val url = "https://torist.my.id/api/get_cart_items.php?user_id=$user_id"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    _keranjangItems = response
                    Log.d("pzn keranjang",response.toString())
                    _liveKeranjangItems.postValue(response)
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                Log.d("err",error.toString())
                callback(false)
            })
        requestQueue.add(jsonArrayRequest)
    }

    fun addToDB(prd_id : Int, user_id : Int, context : Context){
        val params = JSONObject()
        val url = "https://torist.my.id/api/add_cart_item.php"

        params.put("user_id",user_id)
        params.put("product_id", prd_id)

        Log.d("pzn", params.toString())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, params,
            { response ->
                val status = response.getString("status")
                Log.d("pzn add keranjang item",status)
                if (status == "berhasil"){
                    Toast.makeText(context, "Produk berhasi ditambahkan ke keranjang", Toast.LENGTH_LONG).show()
//                    getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                } else {
                    Toast.makeText(context, "Gagal, $status", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Toast.makeText(context, "eroor", Toast.LENGTH_LONG).show()
            })

        RQ.getRQ().add(jsonObjectRequest)
    }


//    Request menggunakan String Request
    fun addToDB2(prd_id : Int, user_id : Int, context : Context){
        val url = "https://torist.my.id/api/add_cart_item.php"

        val postData = HashMap<String, String>()
        postData.put("product_id",prd_id.toString())
        postData.put("user_id",user_id.toString())

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                val resp = response.trim()
                if (resp == "berhasil"){
                    Toast.makeText(context, "Produk berhasi ditambahkan ke keranjang", Toast.LENGTH_LONG).show()
//                    getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
                } else {
                    Toast.makeText(context, "Gagal, $resp", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Toast.makeText(context, "eroor", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return postData
            }
        }
        RQ.getRQ().add(stringRequest)
    }

    fun getTokoArrayList() : ArrayList<Toko> {
        var list = ArrayList<Toko>()
        Log.d("pzn lenght", _keranjangItems.length().toString())
        try {
            for( i in 0 until _keranjangItems.length()){
                val item = _keranjangItems.getJSONObject(i)
                val index = list.size
                Log.d("pzn index",index.toString())
                if (index != 0){
                    val toko_terakhir = list[index-1].toko_name
                    Log.d("pzn toko terakhir",toko_terakhir)
                    Log.d("pzn toko sekarang",item.getString("shop_name"))
                    if (item.getString("shop_name") != toko_terakhir){
                        list.add(Toko(item.getString("shop_id"),item.getString("shop_name"),item.getString("shop_address")))
                    }
                } else {
                    list.add(Toko(item.getString("shop_id"),item.getString("shop_name"),item.getString("shop_address")))
                }
            }
        }
        catch (e : Exception){
            Log.d("pzn ker catch", e.toString())
        }
        return list
    }

    fun getProductArrayListPerToko(toko_id : String) : ArrayList<ProdukKeranjang> {
        var list = ArrayList<ProdukKeranjang>()
        Log.d("pzn lenght", _keranjangItems.length().toString())
        try {
            for( i in 0 until _keranjangItems.length()){
                val item = _keranjangItems.getJSONObject(i)
                if (item.getString("shop_id") == toko_id){
                    list.add(ProdukKeranjang(
                        item.getString("cart_id"),
                        item.getString("shop_name"),
                        item.getString("product_category"),
                        item.getString("product_name"),
                        item.getString("product_price").toInt(),
                        item.getString("cart_quantity").toInt(),
                        item.getString("product_desc"),
                        item.getString("product_photo")
                        )
                    )
                }
            }
        }
        catch (e : Exception){
            Log.d("pzn ker catch", e.toString())
        }

        return list
    }

}