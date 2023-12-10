package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.TokoProdukAdapater
import com.example.torist.data.TokoProduct
import com.example.torist.databinding.ActivityTokoProdukBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.TokoProducts

class TokoProdukActivity : AppCompatActivity() {

    lateinit var binding : ActivityTokoProdukBinding

    private lateinit var tokoProdukAdapter : TokoProdukAdapater
    private lateinit var recyclerView: RecyclerView
    private var tokoProdukArrayList = ArrayList<TokoProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokoProducts.getFromDB(LoggedInUser.idToko.toString())
        binding.loadingProgressBar.visibility = View.VISIBLE

        binding.btnTambahProduk.setOnClickListener {
            startActivity(Intent(this, TokoTambahProdukActivity::class.java))
        }

        recyclerView = binding.RVtokoProduk
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        tokoProdukAdapter = TokoProdukAdapater(tokoProdukArrayList)
        recyclerView.adapter = tokoProdukAdapter

        TokoProducts.liveTokoProducts.observe(this, {
            tokoProdukAdapter.list = TokoProducts.getArrayList()
            tokoProdukAdapter.notifyDataSetChanged()
            Log.d("pzn observe", "observe")
            binding.loadingProgressBar.visibility = View.GONE
        })

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }

    }
}