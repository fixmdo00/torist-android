package com.example.torist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.torist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("start_fragment") != null){
            val fragment = intent.getStringExtra("start_fragment")
            when (fragment){
                "keranjang" -> {
                    replaceFragment(KeranjangFragment())
                    binding.bottomNavigationView.selectedItemId = R.id.keranjang}
                "pesanan" -> {
                    replaceFragment(PesananFragment())
                    binding.bottomNavigationView.selectedItemId = R.id.pesanan}
                "profil" -> {
                    replaceFragment(ProfilFragment())
                    binding.bottomNavigationView.selectedItemId = R.id.profil}
            }
        } else {
            replaceFragment(BerandaFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.beranda-> replaceFragment(BerandaFragment())
                R.id.keranjang-> replaceFragment(KeranjangFragment())
                R.id.pesanan-> replaceFragment(PesananFragment())
                R.id.profil-> replaceFragment(ProfilFragment())

                else -> {

                }
            }
            true
        }
    }

    fun replaceFragment (fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}

