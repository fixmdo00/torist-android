package com.example.torist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.torist.databinding.FragmentProfilBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.SP

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfilFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentProfilBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(layoutInflater, container, false)

        val userProfile = LoggedInUser.loggedInUser

        binding.tvUsername.text = userProfile.username
        binding.tvNama.text = userProfile.nama
        binding.tvTelp.text = userProfile.tlp
        binding.tvAlamat.text = userProfile.alamat

        binding.btnKeluar.setOnClickListener {
            SP.hapusSP()
            LoggedInUser.hapusUser()
//            KeranjangItems.hapusKeranjangItems()
            val intent = Intent(context, SplashScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.btnToko.setOnClickListener {
//            replaceFragment(TokoFragment())
            if (LoggedInUser.idToko == null) {
                val intentDaftarToko = Intent(context, TokoDaftarActivity::class.java)
                startActivity(intentDaftarToko)
            } else {
                val intentToko = Intent(context, TokoActivity::class.java)
                startActivity(intentToko)
            }
        }


        return binding.root
    }

    fun replaceFragment (fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}