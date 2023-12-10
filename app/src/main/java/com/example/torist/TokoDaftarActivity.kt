package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.torist.databinding.ActivityTokoDaftarBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import com.example.torist.`object`.SP
import org.json.JSONObject

class TokoDaftarActivity : AppCompatActivity() {

    lateinit var binding : ActivityTokoDaftarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val params = JSONObject()
        val url = "https://torist.my.id/api/register_shop.php"

        binding.btnDaftar.setOnClickListener {

            binding.loadingProgressBar.visibility = View.VISIBLE

            val intentTokoActivity = Intent(this, TokoActivity::class.java)

            params.put("user_id", LoggedInUser.loggedInUser.id.toString())
            params.put("nama_toko", binding.itNamaToko.text.toString())
            params.put("alamat_toko", binding.itAlamatToko.text.toString())
            params.put("nama_bank", binding.itNamaBank.text.toString())
            params.put("nomor_rekening", binding.itNomorRekening.text.toString())
            Log.d("pzn register","button clik")
            Log.d("pzn register",params.toString())

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    binding.loadingProgressBar.visibility = View.GONE

                    val status = response.getString("status")
                    Log.d("pzn register",status)
                    if (status == "berhasil"){
                        LoggedInUser.getTokoInfo(SP.getSP().getString("userId", "null").toString()){
                            when (it){
                                true -> {
                                    Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                                    startActivity(intentTokoActivity)
                                    finish()
                                }
                                false -> {
                                    Toast.makeText(this, "Gagal mengambil data toko", Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(this, "Gagal, $status", Toast.LENGTH_LONG).show()
                    }
                },
                { error ->
                    Log.d("pzn register err",error.toString())
                    Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                })
            RQ.getRQ().add(jsonObjectRequest)
        }
    }
}