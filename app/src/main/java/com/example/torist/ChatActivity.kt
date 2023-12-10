package com.example.torist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.ChatAdapater
import com.example.torist.data.Chat
import com.example.torist.databinding.ActivityChatBinding
import com.example.torist.`object`.Chats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    lateinit var chatAdapter : ChatAdapater
    lateinit var recyclerView : RecyclerView
    lateinit var user_id : String
    lateinit var shop_id : String
    lateinit var user_type : String
    lateinit var nama : String
    lateinit var lastDatetime : String
    private var chatList = ArrayList<Chat>()
    private var refreshing = false
    private var refreshJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastDatetime = "null"
        binding = ActivityChatBinding.inflate(layoutInflater)
        user_id = intent.getStringExtra("user_id").toString()
        shop_id = intent.getStringExtra("shop_id").toString()
        user_type = intent.getStringExtra("user_type").toString()
        nama = intent.getStringExtra("nama").toString()
        binding.tvNama.text = nama
        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }
        Chats.clearList()
        Chats.getChatsFromDb(binding.loadingProgressBar, shop_id, user_id){}
        setContentView(binding.root)
        autoRefreshChat(true)
        recyclerView = binding.RVChat
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        chatAdapter = ChatAdapater(chatList)
        recyclerView.adapter = chatAdapter
        Chats.liveChats.observe(this){
            chatAdapter.list = Chats.getFriendArrayList(user_type)
            Log.d("chat adapter list",chatAdapter.list.toString())
            chatAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
            if (chatAdapter.list.size != 0) {
                lastDatetime = chatAdapter.list[chatAdapter.list.size - 1].datetime
                Log.d("last datetime", lastDatetime)
            }
        }
        Chats.liveDatetime.observe(this){
            refreshChat()
        }
        binding.btnKirim.setOnClickListener {
            if (!binding.itMessage.text.isNullOrBlank()){
                Chats.addNewChat(binding.loadingProgressBar, user_id, shop_id, user_type, binding.itMessage.text.toString()){
                    when (it){
                        true -> {
                            Chats.getChatsFromDb(binding.loadingProgressBar, shop_id, user_id){}
                            binding.itMessage.text.clear()
                        }
                        false -> {
                            Toast.makeText(this, "Gagal mengirimkan pesan", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        autoRefreshChat(false)
        super.onBackPressed()
    }

    private fun autoRefreshChat(isRefreshing: Boolean) {
        refreshing = isRefreshing

        if (isRefreshing) {
            refreshJob = CoroutineScope(Dispatchers.Main).launch {
                while (refreshing) {
                    Log.d("Coroutine", "Refresh")
//                    refreshChat()
                    checkNewUpdate()
                    delay(5000)
                }
            }
        } else {
            refreshJob?.cancel()
        }
    }

    fun refreshChat (){
        Chats.getChatsFromDb(shop_id, user_id){}
    }

    fun checkNewUpdate (){
        Chats.checkNewChatsFromDb(shop_id, user_id, lastDatetime)
    }
}