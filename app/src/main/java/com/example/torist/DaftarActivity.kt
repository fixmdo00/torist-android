package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.torist.databinding.ActivityDaftarBinding
import com.example.torist.`object`.RQ
import org.json.JSONObject

class DaftarActivity : AppCompatActivity() {

    lateinit var binding : ActivityDaftarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val params = JSONObject()
        val url = "https://torist.my.id/api/register_user.php"

        binding.btnDaftar.setOnClickListener {

            binding.loadingProgressBar.visibility = View.VISIBLE

            val intentLoginActivity = Intent(this, LoginActivity::class.java)

            params.put("username", binding.itUsername.text.toString())
            params.put("nama", binding.itNama.text.toString())
            params.put("tlp", binding.itTelp.text.toString())
            params.put("sandi", binding.itPassword.text.toString())
            params.put("alamat", binding.itAlamat.text.toString())
            Log.d("pzn register","button clik")
            Log.d("pzn register",params.toString())


            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    binding.loadingProgressBar.visibility = View.GONE

                    val status = response.getString("status")
                    Log.d("pzn register",status)
                    if (status == "berhasil"){
                        Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_LONG).show()
                        startActivity(intentLoginActivity)
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