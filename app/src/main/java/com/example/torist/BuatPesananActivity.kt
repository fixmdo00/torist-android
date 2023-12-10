package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.torist.adapter.BuatPesananProdukAdapater
import com.example.torist.data.ProdukKeranjang
import com.example.torist.databinding.ActivityBuatPesananBinding
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class BuatPesananActivity : AppCompatActivity() {

    lateinit var binding : ActivityBuatPesananBinding

    lateinit var produkAdapter : BuatPesananProdukAdapater
    lateinit var recyclerView: RecyclerView
    var produkArrayList = ArrayList<ProdukKeranjang>()


    private val _liveOngkir = MutableLiveData<Int>().apply {
        0
    }
    val liveOngkir: LiveData<Int> = _liveOngkir

    override fun onResume() {
        super.onResume()
        val daftarProvinsi = resources.getStringArray(R.array.provinsi)
        val arrayAdapater = ArrayAdapter(this, R.layout.item_dropdown_provinsi, daftarProvinsi)
        binding.itProvinsi.setAdapter(arrayAdapater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBuatPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.RVProdukBuatPesanan.isNestedScrollingEnabled = false

        val idToko = intent.getStringExtra("id_toko")!!
        val _liveprodukArrayList = MutableLiveData<ArrayList<ProdukKeranjang>>()
        val liveprodukArrayList: LiveData<ArrayList<ProdukKeranjang>> = _liveprodukArrayList

        val _totalHarga = getTotalHarga(Keranjang.getProductArrayListPerToko(idToko))
        val totalHarga = "Rp " + NumberFormat.getNumberInstance(Locale.getDefault()).format(_totalHarga)
        var provinsi = "null"
        var ongkir = 0

        binding.btnKembali.setOnClickListener {
            onBackPressed()
        }
        _liveprodukArrayList.postValue(
            Keranjang.getProductArrayListPerToko(idToko)
        )

        recyclerView = binding.RVProdukBuatPesanan
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        produkAdapter = BuatPesananProdukAdapater(produkArrayList, binding.loadingProgressBar)
        recyclerView.adapter = produkAdapter

        liveprodukArrayList.observe(this){
            produkAdapter.list = it
            produkAdapter.notifyDataSetChanged()
        }

        binding.tvTotalHarga1.text = totalHarga
        binding.tvTotalHarga2.text = totalHarga

        binding.itKotaKabupaten.setOnEditorActionListener { _, actionId, _ ->
            Log.d("pzn done1",actionId.toString())
            Log.d("pzn done2", EditorInfo.IME_ACTION_DONE.toString())
            binding.itKotaKabupaten.clearFocus()
            return@setOnEditorActionListener false
        }

        ubahTotalHarga(0, _totalHarga)

        liveOngkir.observe(this){
            ubahTotalHarga(it,_totalHarga)
            binding.tvOngkirPreview.visibility = View.VISIBLE
        }

        val daftarProvinsi = resources.getStringArray(R.array.provinsi)
        val arrayAdapater = ArrayAdapter(this, R.layout.item_dropdown_provinsi, daftarProvinsi)
        binding.itProvinsi.setAdapter(arrayAdapater)
        binding.itProvinsi.setOnItemClickListener { adapterView, view, i, l ->
            Log.d("pzn dropdown",adapterView.getItemAtPosition(i).toString())
            provinsi = adapterView.getItemAtPosition(i).toString()
            if (provinsi == "Sulawesi Utara"){
                ongkir = 15000
            } else {
                ongkir = 65000
            }
            _liveOngkir.postValue(ongkir)
        }

        binding.btnBuatPesanan.setOnClickListener {
            if (
                provinsi == "null" ||
                binding.itKotaKabupaten.text.toString().isEmpty() ||
                binding.itNamaJalan.text.toString().isEmpty() ||
                binding.itNamaPenerima.text.toString().isEmpty() ||
                binding.itTelpPenerima.text.toString().isEmpty()
            ){
                Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_LONG).show()
            } else {
                buatPesanan(
                    LoggedInUser.loggedInUser.id,
                    idToko,
                    provinsi,
                    binding.itKotaKabupaten.text.toString(),
                    binding.itNamaJalan.text.toString(),
                    binding.itNamaPenerima.text.toString(),
                    binding.itTelpPenerima.text.toString(),
                    binding.itCatatan.text.toString(),
                    ongkir.toString()
                )
            }
        }
    }

    private fun buatPesanan(user_id: Int?, id_toko: String, provinsi: String, kota : String, detail_alamat : String, nama_penerima : String, telp_penerima: String, catatan : String, ongkir : String) {
        val url = "https://torist.my.id/api/add_new_order.php"
        binding.loadingProgressBar.visibility = View.VISIBLE
        val postData = HashMap<String, String>()
        postData.put("user_id",user_id.toString())
        postData.put("cart_shop_id",id_toko)
        postData.put("provinsi", provinsi)
        postData.put("kota", kota)
        postData.put("detail_alamat", detail_alamat)
        postData.put("nama_penerima", nama_penerima)
        postData.put("telp_penerima", telp_penerima)
        postData.put("catatan", catatan)
        postData.put("ongkir", ongkir)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                binding.loadingProgressBar.visibility = View.GONE
                val resp = response.trim()
                if (resp == "berhasil"){
                    Toast.makeText(this, "Pesanan Berhasil Dibuat", Toast.LENGTH_LONG).show()
                    Keranjang.getItemsFromDB(user_id.toString())
                    intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("start_fragment", KERANJANG_FRAGMENT)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Gagal, $resp", Toast.LENGTH_LONG).show()
                    Log.d("pzn",resp)
                }
            },
            Response.ErrorListener { error ->
                Log.d("pzn add keranjang item err",error.toString())
                Toast.makeText(this, "eroor", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return postData
            }
        }
        RQ.getRQ().add(stringRequest)
    }

    private fun ubahTotalHarga(it: Int, _totalHarga: Int) {
        val ongkir = NumberFormat.getNumberInstance(Locale.getDefault()).format(it).toString()
        val ongkirPlusProduk = NumberFormat.getNumberInstance(Locale.getDefault()).format(it + _totalHarga).toString()
        binding.tvOngkirPreview.text = "Biaya Pengiriman Rp " + ongkir
        binding.tvBiayaPengiriman.text = "Rp " + ongkir
        binding.tvTotalSemuaHarga.text = ongkirPlusProduk
    }

    private fun getTotalHarga(list : ArrayList<ProdukKeranjang>) : Int {
        val len = list.size
        Log.d("xxx", len.toString())
        var total : Int = 0
        for ( i in 0 until len){
            Log.d("xxx", list[i].toString())
            total += list[i].harga * list[i].jumlah
        }
        return total
    }

    companion object{
        val KERANJANG_FRAGMENT = "keranjang"
        val PESANAN_FRAGMENT = "pesanan"
    }
}