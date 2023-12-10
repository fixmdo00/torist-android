package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.example.torist.adapter.DetailPesananProdukAdapater
import com.example.torist.data.ProdukDetailPesanan
import com.example.torist.databinding.ActivityTokoDetailPesananBinding
import com.example.torist.`object`.Pesanan
import com.example.torist.`object`.RQ
import com.example.torist.`object`.StatusPembayaran
import com.example.torist.`object`.StatusPesanan
import com.example.torist.`object`.TokoPesanan
import java.text.NumberFormat
import java.util.Locale

class TokoDetailPesananActivity : AppCompatActivity() {

    lateinit var binding : ActivityTokoDetailPesananBinding

    lateinit var produkAdapter : DetailPesananProdukAdapater
    lateinit var recyclerView: RecyclerView
    var produkList = ArrayList<ProdukDetailPesanan>()

    lateinit var detailPesanan : com.example.torist.data.Pesanan
    lateinit var order_id : String

    val _liveUpdate =  MutableLiveData<String>()
    val liveUpdate : LiveData<String> = _liveUpdate

    val _liveProductList = MutableLiveData<ArrayList<ProdukDetailPesanan>>()
    val liveProductList: LiveData<ArrayList<ProdukDetailPesanan>> = _liveProductList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RVProdukDetailPesanan.isNestedScrollingEnabled = false

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }

        order_id = intent.getStringExtra("id_pesanan").toString()
        if (order_id.isNotBlank()) {
            updateDetailPesanan(order_id)
            updateProductList(order_id)
        }

        recyclerView = binding.RVProdukDetailPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        produkAdapter = DetailPesananProdukAdapater(produkList, binding.loadingProgressBar)
        recyclerView.adapter = produkAdapter

        liveProductList.observe(this){
            produkAdapter.list = it
            produkAdapter.notifyDataSetChanged()
            val total_harga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(
                TokoPesanan.getTotalHarga())
            binding.tvTotalHarga1.text = total_harga
            binding.tvTotalHarga2.text = total_harga
        }

        liveUpdate.observe(this){
            val biaya_pengiriman = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(detailPesanan.order_delivery_price)
            val _total_semua_harga = detailPesanan.order_total_price + detailPesanan.order_delivery_price
            val total_semua_harga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(_total_semua_harga)

            binding.tvStatus.text = StatusPesanan.getTokoStatus(detailPesanan.order_status, binding.pengirimanContainer, binding.pengirimanContainerTv)
            binding.tvAlamatPengiriman.text = detailPesanan.order_address
            binding.tvNamaPenerima.text = detailPesanan.order_name
            binding.tvTlpPenerima.text = detailPesanan.order_telp
            binding.tvBiayaPengiriman.text = biaya_pengiriman
            binding.tvTotalSemuaHarga.text = total_semua_harga
            binding.tvStatusPembayaran.text = StatusPembayaran.getTokoStatus(detailPesanan.order_status, binding.btnlihatBuktiBayar, binding.btnKonfirBuktiBayar)
            binding.tvKurir.text = detailPesanan.order_courier
            binding.tvResi.text = detailPesanan.order_resi
            binding.tvCatatan.text = detailPesanan.order_note

            binding.btnlihatBuktiBayar.setOnClickListener {
                val intent = Intent(this, LihatBuktiBayarActivity::class.java)
                intent.putExtra("link",detailPesanan.order_payment_photo)
                startActivity(intent)
            }

            binding.btnKonfirBuktiBayar.setOnClickListener {
                konfirmasiPembayaran(order_id){}
            }

            binding.btnKonfirmasiPengiriman.setOnClickListener {
                konfirmasiPengiriman(order_id,binding.itKurir.text.toString(), binding.itNomorResi.text.toString()){}
            }
        }



        binding.btnSelesaikanPesanan.isEnabled = false
    }

    private fun konfirmasiPengiriman(order_id: String, kurir: String, resi: String, callback: () -> Unit) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/update_konfirmasi_pengiriman.php"
        val postData = HashMap<String, String>()
        postData.put("order_id",order_id)
        postData.put("order_kurir",kurir)
        postData.put("order_resi",resi)
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener { response ->
                val resp = response.trim()
                if (resp == "berhasil"){
                    Toast.makeText(this, "Pengiriman berhasil di konfirmasi", Toast.LENGTH_LONG).show()
                    updateDetailPesanan(order_id)
                } else {
                    Toast.makeText(this, "Gagal, $resp", Toast.LENGTH_LONG).show()
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "eroor", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return postData
            }
        }
        RQ.getRQ().add(stringRequest)

    }

    private fun konfirmasiPembayaran(order_id: String, callback: () -> Unit) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/update_konfirmasi_pembayaran.php"
        val postData = HashMap<String, String>()
        postData.put("order_id",order_id)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener { response ->
                val resp = response.trim()
                if (resp == "berhasil"){
                    Toast.makeText(this, "Pembayaran berhasil di konfirmasi", Toast.LENGTH_LONG).show()
                    updateDetailPesanan(order_id)
                } else {
                    Toast.makeText(this, "Gagal, $resp", Toast.LENGTH_LONG).show()
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "eroor", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return postData
            }
        }
        RQ.getRQ().add(stringRequest)
    }

    override fun onResume() {
        super.onResume()
        updateDetailPesanan(order_id)
    }

    override fun onRestart() {
        super.onRestart()
        updateDetailPesanan(order_id)
    }

    fun updateDetailPesanan(id : String){
        TokoPesanan.getFromDB(binding.loadingProgressBar) {
            when (it) {
                true -> {
                    TokoPesanan.livePesanan.observe(this) {
                        detailPesanan = TokoPesanan.getPesananDetail(id)
                        _liveUpdate.postValue("updated")
                    }
                }
                false -> {}
            }
        }
    }

    fun updateProductList(id : String){
        TokoPesanan.getProductListFromDB(id, binding.loadingProgressBar){
            when(it){
                true -> {
                    _liveProductList.postValue(TokoPesanan.getProductsArrayList())
                }
                false ->{}
            }
        }
    }
}