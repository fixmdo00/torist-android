package com.example.torist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.torist.databinding.FragmentProdukDetailBinding
import com.example.torist.databinding.FragmentProdukKategoriBinding
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.Products
import com.example.torist.`object`.RQ
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProdukDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProdukDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentProdukDetailBinding
    private val binding get() = _binding

    var productDetail = JSONObject()
    lateinit var shopId : String

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
        _binding = FragmentProdukDetailBinding.inflate(layoutInflater,container, false)
        var id = String()
        val bundle = arguments
        if (bundle != null) {
            Log.d("pzn", bundle.getString("param1").toString())
            binding.tvNamaProduk.text = bundle.getString("param1")
            id = bundle.getString("param2").toString()
        }

        getData(id, binding.loadingProgressBar) {
            if (it) {
                val harga : Int = productDetail.getString("product_price").toInt()
                binding.tvHargaProduk.text = "Rp. " + NumberFormat.getNumberInstance(Locale.getDefault()).format(harga)
                binding.tvDeskProduk.text = productDetail.get("product_desc").toString()
                binding.tvNamaToko.text = productDetail.get("shop_name").toString()
                binding.tvAlamatToko.text = productDetail.get("shop_address").toString()
                shopId = productDetail.getString("shop_id")
                Picasso.get()
                    .load("https://torist.my.id" + productDetail.get("product_photo").toString())
                    .placeholder(R.drawable.loading_background)
                    .into(binding.ivFotoProduk)
            }
            else {}
        }

        binding.btnChat.setOnClickListener {
            val chatIntent = Intent(context, ChatActivity::class.java)
            chatIntent.putExtra("user_id", LoggedInUser.loggedInUser.id.toString())
            chatIntent.putExtra("shop_id", productDetail.getString("shop_id"))
            chatIntent.putExtra("user_type", "user")
            chatIntent.putExtra("nama", productDetail.getString("shop_name"))
            startActivity(chatIntent)
//            halo
        }

        binding.btnIVkembali.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnTambahKeranjang.setOnClickListener {
            Keranjang.addToDB2(id.toInt(),LoggedInUser.loggedInUser.id!!.toInt(), requireContext().applicationContext)
        }

        return binding.root
    }

    private fun getData(id: String, progressBar : ProgressBar, callback: (Boolean) -> Unit) {
        progressBar.visibility = View.VISIBLE
        val url = "https://torist.my.id/api/get_product_detail.php?id=$id"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                try {
                    // Ambil data pertama dari JSON Array
                    productDetail= response[0] as JSONObject
                    callback(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            { error ->
                Log.d("err",error.toString())
                callback(false)
            }
        )
        RQ.getRQ().add(jsonArrayRequest)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProdukDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProdukDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}