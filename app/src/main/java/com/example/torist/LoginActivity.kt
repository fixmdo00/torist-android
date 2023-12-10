package com.example.torist

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.databinding.ActivityLoginBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import com.example.torist.`object`.SP
import org.json.JSONException

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var SPeditor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentDaftarActivity = Intent(this, DaftarActivity::class.java)
        val username = binding.itUsername
        val password = binding.itPassword

        SPeditor = SP.getSP().edit()

        binding.textBtnDaftar.setOnClickListener {
            startActivity(intentDaftarActivity)
        }

        binding.btnLogin.setOnClickListener {
            binding.loadingProgressBar.visibility = View.VISIBLE
            login(username.text.toString(), password.text.toString())
        }


    }


    private fun login (username : String, password : String) {

        validateUser(username, password) {
                isValid ->
            if (isValid) {
                Log.d("pzn", "login berhasil")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                LoggedInUser.getUserInfo(SP.getSP().getString("userId", "null").toString()){
                    binding.loadingProgressBar.visibility = View.GONE
                    when (it){
                        true -> {
                            startActivity(intent)
                            finish()
                        }
                        false -> { }
                    }
                }
            } else {
                binding.loadingProgressBar.visibility = View.GONE
                Log.d("pzn", "login gagal")
                Toast.makeText(this,"Username atau Password salah", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateUser (username : String, password : String,callback: (Boolean) -> Unit) {

        val url = "https://torist.my.id/api/validate_user.php?username=$username&password=$password"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val status = response.getJSONObject(0).getString("status")

                    when (status) {
                        "berhasil" -> {
                            SPeditor.putBoolean("loginStatus", true)
                            SPeditor.putString("userId", response.getJSONObject(1).getString("user_id"))
                            SPeditor.apply()
                            callback(true)
                        }
                        "user salah" , "password salah" -> {
                            callback(false)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                binding.loadingProgressBar.visibility = View.GONE
                Log.d("err",error.toString())
            })
        RQ.getRQ().add(jsonArrayRequest)
    }
}