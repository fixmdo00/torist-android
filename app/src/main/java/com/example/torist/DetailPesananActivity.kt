package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.example.torist.adapter.DetailPesananProdukAdapater
import com.example.torist.data.ProdukDetailPesanan
import com.example.torist.data.ProdukKeranjang
import com.example.torist.databinding.ActivityBuatPesananBinding
import com.example.torist.databinding.ActivityDetailPesananBinding
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.Pesanan
import com.example.torist.`object`.RQ
import com.example.torist.`object`.StatusPembayaran
import com.example.torist.`object`.StatusPesanan
import java.text.NumberFormat
import java.util.Locale

class DetailPesananActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailPesananBinding

    lateinit var produkAdapter : DetailPesananProdukAdapater
    lateinit var recyclerView: RecyclerView
    var produkList = ArrayList<ProdukDetailPesanan>()

    lateinit var detailPesanan : com.example.torist.data.Pesanan
    lateinit var order_id : String

    val _liveUpdate =  MutableLiveData<String>()
    val liveUpdate : LiveData<String> = _liveUpdate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RVProdukDetailPesanan.isNestedScrollingEnabled = false

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }

        val _liveProductList = MutableLiveData<ArrayList<ProdukDetailPesanan>>()
        val liveProductList: LiveData<ArrayList<ProdukDetailPesanan>> = _liveProductList

        order_id = intent.getStringExtra("id_pesanan").toString()
        if (order_id.isNotBlank()) {

            updateDetailPesanan(order_id)

            Pesanan.getProductListFromDB(order_id!!, binding.loadingProgressBar){
                when(it){
                    true -> {
                        _liveProductList.postValue(Pesanan.getProductsArrayList())
                    }
                    false ->{}
                }
            }
        }

        recyclerView = binding.RVProdukDetailPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        produkAdapter = DetailPesananProdukAdapater(produkList, binding.loadingProgressBar)
        recyclerView.adapter = produkAdapter

        liveProductList.observe(this){
            produkAdapter.list = it
            produkAdapter.notifyDataSetChanged()
            val total_harga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(Pesanan.getTotalHarga())
            binding.tvTotalHarga1.text = total_harga
            binding.tvTotalHarga2.text = total_harga
        }

        liveUpdate.observe(this){
            val biaya_pengiriman = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(detailPesanan.order_delivery_price)
            val _total_semua_harga = detailPesanan.order_total_price + detailPesanan.order_delivery_price
            val total_semua_harga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(_total_semua_harga)

            binding.tvStatus.text = StatusPesanan.getStatus(detailPesanan.order_status, binding.pengirimanContainerTv, binding.containerBtnSelesaikanPesanan, binding.containerBtnBatalkanPesanan)
            binding.tvAlamatPengiriman.text = detailPesanan.order_address
            binding.tvNamaPenerima.text = detailPesanan.order_name
            binding.tvTlpPenerima.text = detailPesanan.order_telp
            binding.tvBiayaPengiriman.text = biaya_pengiriman
            binding.tvTotalSemuaHarga.text = total_semua_harga
            binding.tvStatusPembayaran.text = StatusPembayaran.getStatus(detailPesanan.order_status, binding.btnKirimBuktiBayar, binding.containerInfoRekening)
            binding.tvKurir.text = detailPesanan.order_courier
            binding.tvResi.text = detailPesanan.order_resi
            binding.tvNamaBank.text = detailPesanan.shop_bank
            binding.tvRekeningBank.text = detailPesanan.shop_bank_account
            binding.tvCatatan.text = detailPesanan.order_note

            binding.btnKirimBuktiBayar.setOnClickListener {
                when(detailPesanan.order_status){
                    "1" -> {
                        val intent = Intent(this, PembayaranActivity::class.java)
                        intent.putExtra("order_id",detailPesanan.order_id)
                        startActivity(intent)
                    }
                    "2","3","4","5"->{
                        val intent = Intent(this, LihatBuktiBayarActivity::class.java)
                        intent.putExtra("link",detailPesanan.order_payment_photo)
                        startActivity(intent)
                    }
                }
            }
            binding.btnSelesaikanPesanan.setOnClickListener {
                selesaikanPesanan(order_id)
            }
        }
    }

    private fun selesaikanPesanan(order_id: String) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/update_selesaikan_pesanan.php"
        val postData = HashMap<String, String>()
        postData.put("order_id",order_id)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            com.android.volley.Response.Listener { response ->
                val resp = response.trim()
                if (resp == "berhasil"){
                    Toast.makeText(this, "Pesanan selesai", Toast.LENGTH_LONG).show()
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

    private fun updateDetailPesanan(order_id : String){
        Pesanan.getFromDB(binding.loadingProgressBar){
            when(it) {
                true -> {
                    Pesanan.livePesanan.observe(this){
                        detailPesanan = Pesanan.getPesananDetail(order_id)
                        _liveUpdate.postValue("updated")
                    }
                }
                false -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (order_id.isNotBlank()) {

            Pesanan.getFromDB(binding.loadingProgressBar) {
                when (it) {
                    true -> {
                        Pesanan.livePesanan.observe(this) {
                            detailPesanan = Pesanan.getPesananDetail(order_id!!)
                            _liveUpdate.postValue("updated")
                        }
                    }
                    false -> {}
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (order_id.isNotBlank()) {

            Pesanan.getFromDB(binding.loadingProgressBar) {
                when (it) {
                    true -> {
                        Pesanan.livePesanan.observe(this) {
                            detailPesanan = Pesanan.getPesananDetail(order_id!!)
                            _liveUpdate.postValue("updated")
                        }
                    }
                    false -> {}
                }
            }
        }
    }
}