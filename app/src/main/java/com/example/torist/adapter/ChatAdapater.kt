package com.example.torist.adapter

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.R
import com.example.torist.data.Chat
import com.example.torist.data.ChatFriend
import com.example.torist.data.ProdukDetailPesanan
import com.example.torist.`object`.CONS
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapater (private val chatFriendList:ArrayList<Chat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((Array<String>) -> Unit)? = null
    var list = chatFriendList

    companion object {
        const val VIEW_TYPE_ME = 1
        const val VIEW_TYPE_FRIEND = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ME -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_kirim, parent, false)
                MyViewHolder(itemView)
            }
            VIEW_TYPE_FRIEND -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_terima, parent, false)
                FriendViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = list[position]
        val currentTime = System.currentTimeMillis() // Waktu saat ini dalam milidetik

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val itemTime = dateFormatter.parse(currentItem.datetime)?.time ?: 0 // Waktu dari data dalam milidetik

        val isToday = DateUtils.isToday(itemTime)

        when (holder.itemViewType) {
            VIEW_TYPE_ME -> {
                val myHolder = holder as MyViewHolder
                myHolder.tvPesan.text = currentItem.pesan.trim()
                myHolder.tvWaktu.text = if (isToday) {
                    // Tampilkan hh:mm jika waktu masih hari ini
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(itemTime)
                } else {
                    // Tampilkan mm-dd hh:mm jika waktu lebih lama dari hari ini
                    SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(itemTime)
                }
            }
            VIEW_TYPE_FRIEND -> {
                val friendHolder = holder as FriendViewHolder
                friendHolder.tvPesan.text = currentItem.pesan
                friendHolder.tvWaktu.text = if (isToday) {
                    // Tampilkan hh:mm jika waktu masih hari ini
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(itemTime)
                } else {
                    // Tampilkan mm-dd hh:mm jika waktu lebih lama dari hari ini
                    SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(itemTime)
                }
            }
        }
    }

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val currentItem = list[position]
//        when (holder.itemViewType) {
//            VIEW_TYPE_ME -> {
//                Log.d("my display","displayed")
//                val myHolder = holder as MyViewHolder
//                myHolder.tvPesan.text = currentItem.pesan
//                myHolder.tvWaktu.text = currentItem.datetime
//            }
//            VIEW_TYPE_FRIEND -> {
//                Log.d("your display","displayed")
//                val friendHolder = holder as FriendViewHolder
//                friendHolder.tvPesan.text = currentItem.pesan
//                friendHolder.tvWaktu.text = currentItem.datetime
//            }
//        }
//    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentItem = list[position]
        return if (currentItem.sender == "me") {
            VIEW_TYPE_ME
        } else {
            VIEW_TYPE_FRIEND
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPesan: TextView = itemView.findViewById(R.id.itemChatKirimTvChat)
        val tvWaktu: TextView = itemView.findViewById(R.id.itemChatKirimTvWaktu)
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPesan: TextView = itemView.findViewById(R.id.itemChatTerimaTvChat)
        val tvWaktu: TextView = itemView.findViewById(R.id.itemChatTerimaTvWaktu)
    }
}