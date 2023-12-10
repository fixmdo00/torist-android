package com.example.torist.`object`

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.example.torist.data.Chat
import org.json.JSONArray
import org.json.JSONException

object Chats {

    var _chats = JSONArray()
    private val _liveChats = MutableLiveData<JSONArray>()
    val liveChats : LiveData<JSONArray> = _liveChats

    var _lastDatetime = String()
    private val _liveDatetime = MutableLiveData<String>()
    val liveDatetime : LiveData<String> = _liveDatetime

    fun getChatsFromDb(progressBar: ProgressBar, shopId : String, userId : String, callback : (Boolean) -> Unit){
        progressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/get_chat.php?action=getchatlist&current_user_id=$userId&current_shop_id=$shopId"
        Log.d("url list",url.toString())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    clearList()
                    _chats = response
                    _liveChats.postValue(response)
                    Log.d("url respon",response.toString())
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                callback(false)
                Log.d("err",error.toString())
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    fun getChatsFromDb(shopId : String, userId : String, callback : (Boolean) -> Unit){

        val url = "https://torist.my.id/api/get_chat.php?action=getchatlist&current_user_id=$userId&current_shop_id=$shopId"
        Log.d("url list",url.toString())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                try {
                    // Ambil data pertama dari JSON Array
                    clearList()
                    _chats = response
                    _liveChats.postValue(response)
                    Log.d("url respon",response.toString())
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                callback(false)
                Log.d("err",error.toString())
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    fun checkNewChatsFromDb(shopId : String, userId : String, currentLastChat : String ){

        val url = "https://torist.my.id/api/get_chat.php?action=getlastchat&current_user_id=$userId&current_shop_id=$shopId"
        Log.d("url list",url.toString())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    when (response.getJSONObject(0).getString("datetime")){
                        currentLastChat -> {}
                        else -> {
                            _lastDatetime = response.getJSONObject(0).getString("datetime")
                            _liveDatetime.postValue(response.getJSONObject(0).getString("datetime"))
                        }
                    }
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

    fun addNewChat(progressBar: ProgressBar, user_id : String, shop_id : String, sender : String, message : String, callback: (Boolean) -> Unit){
        progressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/get_chat.php?action=addnewchat"
        val postData = HashMap<String, String>()
        postData.put("user_id",user_id)
        postData.put("shop_id",shop_id)
        postData.put("sender",sender)
        postData.put("message",message)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener { response ->
                val resp = response.trim()
                Log.d("resp",resp)
                Log.d("resp url",url)
                if (resp == "berhasil"){
                    callback(true)
                } else {
                    callback(false)
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Log.d("error add new chat", error.toString())
                callback(false)
            }) {
            override fun getParams(): Map<String, String> {
                return postData
            }
        }
        RQ.getRQ().add(stringRequest)
    }

    fun clearList (){
        _chats = JSONArray()
        _liveChats.value = JSONArray()
    }

    fun getFriendArrayList(userType : String) : ArrayList<Chat> {
        var list = ArrayList<Chat>()
        try {
            for( i in 0 until _chats.length()){
                val item = _chats.getJSONObject(i)
                if(userType == "shop"){
                    val sender = when (item.getString("sender")) {
                        "shop" -> CONS.ME
                        "user" -> CONS.FRIEND
                        else -> "unknown"
                    }
                    list.add(
                        Chat(
                            item.getString("chat_id"),
                            item.getString("message"),
                            item.getString("datetime"),
                            sender
                        )
                    )
                } else if (userType == "user") {
                    val sender = when (item.getString("sender")) {
                        "shop" -> CONS.FRIEND
                        "user" -> CONS.ME
                        else -> "unknown"
                    }
                    list.add(
                        Chat(
                            item.getString("chat_id"),
                            item.getString("message"),
                            item.getString("datetime"),
                            sender
                        )
                    )
                }
            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan produk catch", e.toString())
        }
        Log.d("url respon chat",list.toString())
        return list
    }
}