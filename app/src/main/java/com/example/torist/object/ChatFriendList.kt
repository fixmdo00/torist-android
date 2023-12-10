package com.example.torist.`object`

import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.data.ChatFriend
import org.json.JSONArray
import org.json.JSONException

object ChatFriendList {

    var _friendList = JSONArray()
    private val _liveFriendList = MutableLiveData<JSONArray>()
    val liveFriendList : LiveData<JSONArray> = _liveFriendList

    fun getFriendListFromDb(progressBar: ProgressBar, userType : String, callback : (Boolean) -> Unit){
        progressBar.visibility = View.VISIBLE
        var currentUserId = String()
        if (userType == "shop"){
            currentUserId = LoggedInUser.idToko.toString()
        }
        else if (userType == "user"){
            currentUserId = LoggedInUser.loggedInUser.id.toString()
        }
        val url = "https://torist.my.id/api/get_chat.php?action=getfriendlist&current_user_type=$userType&current_user_id=$currentUserId"
        Log.d("url list",url.toString())
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    _friendList = response
                    _liveFriendList.postValue(response)
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

    fun clearFriendList (){
        _friendList = JSONArray()
        _liveFriendList.value = JSONArray()
    }

    fun getFriendArrayList(userType : String) : ArrayList<ChatFriend> {
        var list = ArrayList<ChatFriend>()
        try {
            for( i in 0 until _friendList.length()){
                val item = _friendList.getJSONObject(i)
                if(userType == "shop"){
                    list.add(
                        ChatFriend(
                            item.getString("user_id"),
                            item.getString("shop_id"),
                            item.getString("user_name"),
                            item.getString("message"),
                            item.getString("datetime")
                        )
                    )
                } else if (userType == "user") {
                    list.add(
                        ChatFriend(
                            item.getString("user_id"),
                            item.getString("shop_id"),
                            item.getString("shop_name"),
                            item.getString("message"),
                            item.getString("datetime")
                        )
                    )
                }

            }
        }
        catch (e : Exception){
            Log.d("pzn pesanan produk catch", e.toString())
        }
        Log.d("url respon2",list.toString())
        return list
    }
}