package com.example.torist.`object`

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.data.User
import org.json.JSONException

object LoggedInUser {

    var loggedInUser = User(null,"","","","")

    var idToko : Int? = null
    var namaToko : String = ""
    var alamatToko : String  = ""
    var bankToko : String = ""
    var rekeningBankToko : String = ""

    var _liveLoggedInUser = MutableLiveData<User>().apply {
        loggedInUser
    }

    val liveLoggedInUser : LiveData<User> = _liveLoggedInUser

    fun hapusUser (){
        loggedInUser = User(null,"","","","")
        idToko = null
        namaToko = ""
        alamatToko = ""
    }

    fun getUserInfo (id : String, callback: (Boolean) -> Unit){

        val url = "https://torist.my.id/api/get_user_info.php?user_id=$id"
        Log.d("pzn url",url)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val respon = response.getJSONObject(0)
                    val userId = respon.getString("user_id").toInt()
                    val username = respon.getString("user_username")
                    val nama = respon.getString("user_name")
                    val tlp = respon.getString("user_telp")
                    val alamat = respon.getString("user_address")
                    loggedInUser = User(userId, username, nama, tlp, alamat)
                    _liveLoggedInUser.postValue(User(userId, username, nama, tlp, alamat))
                    getTokoInfo(id){}
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

        RQ.getRQ().add(jsonArrayRequest)

    }

    fun getTokoInfo (id : String, callback: (Boolean) -> Unit){

        val url = "https://torist.my.id/api/get_shop_info.php?user_id=$id"
        Log.d("pzn url gettokoinfo",url)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val respon = response.getJSONObject(0)
                    if (respon == null){
                        callback(false)
                    } else {
                        idToko = respon.getString("shop_id").toInt()
                        namaToko = respon.getString("shop_name")
                        alamatToko = respon.getString("shop_address")
                        bankToko = respon.getString("shop_bank")
                        rekeningBankToko = respon.getString("shop_bank_account")
                        Log.d("pzn url gettokoinfo shop id", respon.getString("shop_id"))
                        callback(true)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                Log.d("err",error.toString())
            })

        RQ.getRQ().add(jsonArrayRequest)

    }

    fun getUserInfo (id : String){

        val url = "https://torist.my.id/api/get_user_info.php?user_id=$id"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val respon = response.getJSONObject(0)
                    val userId = respon.getString("user_id").toInt()
                    val username = respon.getString("user_username")
                    val nama = respon.getString("user_name")
                    val tlp = respon.getString("user_telp")
                    val alamat = respon.getString("user_address")
                    loggedInUser = User(userId, username, nama, tlp, alamat)
                    _liveLoggedInUser.postValue(User(userId, username, nama, tlp, alamat))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.d("err",error.toString())

            })

        RQ.getRQ().add(jsonArrayRequest)

    }

}