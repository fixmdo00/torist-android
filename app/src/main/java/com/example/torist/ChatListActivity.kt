package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.ChatListAdapater
import com.example.torist.data.ChatFriend
import com.example.torist.databinding.ActivityChatListBinding
import com.example.torist.`object`.ChatFriendList

class ChatListActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatListBinding
    lateinit var chatListAdapater : ChatListAdapater
    lateinit var recyclerView: RecyclerView
    lateinit var userType : String
    var chatList = ArrayList<ChatFriend>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userType = intent.getStringExtra("userType").toString()
        ChatFriendList.clearFriendList()
        ChatFriendList.getFriendListFromDb(binding.loadingProgressBar,userType){}
        recyclerView = binding.RVChatList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        chatListAdapater = ChatListAdapater(chatList)
        recyclerView.adapter = chatListAdapater
        ChatFriendList.liveFriendList.observe(this){
            chatListAdapater.list = ChatFriendList.getFriendArrayList(userType)
            chatListAdapater.notifyDataSetChanged()
        }
        chatListAdapater.onItemClick = {
            val chatIntent = Intent(this, ChatActivity::class.java)
            chatIntent.putExtra("user_id", it[0])
            chatIntent.putExtra("shop_id", it[1])
            chatIntent.putExtra("user_type", userType)
            chatIntent.putExtra("nama", it[2])
            startActivity(chatIntent)
        }
//        chatListAdapater.list.add(ChatFriend("Nando","Halo","12:13 pm"))
//        chatListAdapater.list.add(ChatFriend("Seva","Selamat siang","12:16 pm"))
//        chatListAdapater.notifyDataSetChanged()
    }
}