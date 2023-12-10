package com.example.torist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
import com.example.torist.`object`.SP

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        SP.initLoginSP(applicationContext)
        RQ.initRQ(applicationContext)

        val login : Boolean = SP.getLoginStatus()

        Handler().postDelayed({
            val intentMainActivity = Intent(this@SplashScreenActivity, MainActivity::class.java)
            val intentLoginActivity = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            val intentSplashScreenActivity = Intent(this@SplashScreenActivity, SplashScreenActivity::class.java)

            if (login == true){
                Log.d("pzn","loop")
                LoggedInUser.getUserInfo(SP.getSP().getString("userId", "null").toString()){
                    when (it){
                        true -> {
                            startActivity(intentMainActivity)
                            finish()
                        }
                        false -> {
                            Toast.makeText(this,"Masalah Jaringan", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                startActivity(intentLoginActivity)
                finish()
            }
        }, 500)
    }
}