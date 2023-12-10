package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.TokoPesananAdapater
import com.example.torist.databinding.ActivityTokoBinding
import com.example.torist.databinding.ActivityTokoProdukBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.Pesanan
import com.example.torist.`object`.TokoPesanan
import com.example.torist.`object`.TokoProducts

class TokoActivity : AppCompatActivity() {

    lateinit var binding : ActivityTokoBinding

    lateinit var adapter : TokoPesananAdapater
    lateinit var recyclerView: RecyclerView
    var arrayList = ArrayList<com.example.torist.data.Pesanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokoPesanan.getFromDB(binding.loadingProgressBar)

        binding.tvNamaToko.text = LoggedInUser.namaToko
        binding.tvAlamatToko.text = LoggedInUser.alamatToko

        recyclerView = binding.RVtokoPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = TokoPesananAdapater(arrayList, this, binding.loadingProgressBar, this)
        recyclerView.adapter = adapter

        TokoPesanan.livePesanan.observe(this){
            val list = TokoPesanan.getPesananArrayList()
            if(list.size != 0){
                binding.tvTidakAdaPesanan.visibility = View.GONE
            } else {
                binding.tvTidakAdaPesanan.visibility = View.VISIBLE
            }
            adapter.list = list
            adapter.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            TokoPesanan.getFromDB(binding.loadingProgressBar)
        }

        binding.btnProduk.setOnClickListener {
            val intent = Intent(this, TokoProdukActivity::class.java)
            startActivity(intent)
        }
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, ChatListActivity::class.java)
            intent.putExtra("userType","shop")
            startActivity(intent)
        }
    }
}