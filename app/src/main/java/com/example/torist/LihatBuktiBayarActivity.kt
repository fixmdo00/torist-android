package com.example.torist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.torist.databinding.ActivityLihatBuktiBayarBinding
import com.squareup.picasso.Picasso

class LihatBuktiBayarActivity : AppCompatActivity() {

    lateinit var binding : ActivityLihatBuktiBayarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatBuktiBayarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val image_link = intent.getStringExtra("link")

        if (image_link != null){
            Picasso.get()
                .load("https://torist.my.id" + image_link)
                .into(binding.ivBuktiBayar)
        }

        binding.btnKembali.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}