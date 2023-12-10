package com.example.torist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.R
import com.example.torist.data.ChatFriend
import com.example.torist.data.ProdukDetailPesanan
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ChatListAdapater (private val chatFriendList:ArrayList<ChatFriend>) : RecyclerView.Adapter<ChatListAdapater.ProductsViewHolder>() {

    var onItemClick : ((Array<String>) -> Unit)? = null

    var list = chatFriendList //kalau mau gunakan notifyDataSetChanged, yang di ubah variabel ini

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_friend,parent,false)
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        var currentItem = list[position]
        holder.tvNama.text = currentItem.nama
        holder.tvWaktu.text = currentItem.lastChatDateTime
        holder.tvLastChat.text = currentItem.lastChat
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(
                arrayOf(currentItem.user_id,currentItem.shop_id,currentItem.nama)
            )
        }
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val tvNama : TextView = itemView.findViewById(R.id.itemChatFriendName)
        val tvWaktu : TextView = itemView.findViewById(R.id.itemChatFriendTime)
        val tvLastChat : TextView = itemView.findViewById(R.id.itemChatFriendLastChat)
    }


}