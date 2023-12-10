package com.example.torist.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.data.TokoProduct
import org.json.JSONArray
import org.json.JSONException

object TokoProducts {

    var _tokoProducts = JSONArray()

    private val _liveTokoProducts = MutableLiveData<JSONArray>()
    val liveTokoProducts : LiveData<JSONArray> = _liveTokoProducts

    fun getFromDB(id_toko : String){

        val url = "https://torist.my.id/api/get_shop_products.php?shop_id=$id_toko"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ambil data pertama dari JSON Array
                    _tokoProducts = response
                    _liveTokoProducts.postValue(response)
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

    fun getArrayList()  : ArrayList<TokoProduct> {
        val productArray = ArrayList<TokoProduct>()
        Log.d("pzn xx", _tokoProducts.toString())

        try {
            for (i in 0 until _tokoProducts.length()) {
                val jsonObject = _tokoProducts.getJSONObject(i)
                val dataField1 = jsonObject.getString("product_id").toString()
                val dataField2 = jsonObject.getString("product_category").toString()
                val dataField3 = jsonObject.getString("product_name").toString()
                val dataField4 = jsonObject.getString("product_price").toInt()
                val dataField5 = jsonObject.getString("product_desc").toString()
                val dataField6 = jsonObject.getString("product_photo").toString()
                val data = TokoProduct(dataField1, dataField2, dataField3, dataField4, dataField5, dataField6)
                productArray.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return productArray
    }

}