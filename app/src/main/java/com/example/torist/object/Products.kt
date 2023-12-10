package com.example.torist.`object`

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.data.Produk
import org.json.JSONArray
import org.json.JSONException

object Products {

    var _products = JSONArray()

    private val _liveProducts = MutableLiveData<JSONArray>()
    val liveProducts : LiveData<JSONArray> = _liveProducts

    fun getFromDB(progressBar: ProgressBar){
        progressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/get_products.php"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _products = JSONArray()
                    _products = response
                    _liveProducts.postValue(response)
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

    fun getFromDB(kategori : String, progressBar: ProgressBar){
        progressBar.visibility = View.VISIBLE
        clearData()
        val url = "https://torist.my.id/api/get_products.php?kategori=$kategori"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _products = response
                    _liveProducts.postValue(response)
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

    fun clearData (){
        _products = JSONArray()
        _liveProducts.postValue(_products)
    }

    fun getArrayList()  : ArrayList<Produk> {
        val productArray = ArrayList<Produk>()
        Log.d("pzn ", _products.toString())

        try {
            for (i in 0 until _products.length()) {
                val jsonObject = _products.getJSONObject(i)
                val dataField1 = jsonObject.getString("product_id").toString()
                val dataField2 = jsonObject.getString("shop_name").toString()
                val dataField3 = jsonObject.getString("product_category").toString()
                val dataField4 = jsonObject.getString("product_name").toString()
                val dataField5 = jsonObject.getString("product_price").toInt()
                val dataField6 = jsonObject.getString("product_desc").toString()
                val dataField7 = jsonObject.getString("product_photo").toString()
                val data = Produk(dataField1, dataField2, dataField3, dataField4, dataField5, dataField6,dataField7)
                productArray.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return productArray
    }
}