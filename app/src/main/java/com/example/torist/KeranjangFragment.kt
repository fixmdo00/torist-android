package com.example.torist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.KeranjangTokoAdapater
import com.example.torist.data.Produk
import com.example.torist.data.Toko
import com.example.torist.databinding.FragmentBerandaBinding
import com.example.torist.databinding.FragmentKeranjangBinding
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.LoggedInUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [KeranjangFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeranjangFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentKeranjangBinding
    private val binding get() = _binding

    private lateinit var keranjangTokoAdapter : KeranjangTokoAdapater
    private lateinit var recyclerView: RecyclerView
    private var keranjangTokoList = ArrayList<Toko>()

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
        _binding = FragmentKeranjangBinding.inflate(layoutInflater, container, false)
        binding.loadingProgressBar.visibility = View.VISIBLE
        Keranjang.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())

        recyclerView = binding.RVKeranjangPerTokoItem
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        keranjangTokoAdapter = KeranjangTokoAdapater(keranjangTokoList, this, binding.loadingProgressBar, requireContext())
        recyclerView.adapter = keranjangTokoAdapter

        Keranjang.liveKerItems.observe(viewLifecycleOwner, {
            keranjangTokoAdapter.list = Keranjang.getTokoArrayList()
            keranjangTokoAdapter.notifyDataSetChanged()
            binding.loadingProgressBar.visibility = View.GONE
            if(it.length() == 0){
                binding.tvKeranjangKosong.visibility = View.VISIBLE
            } else{
                binding.tvKeranjangKosong.visibility = View.INVISIBLE
            }
        })

//        binding.btn.setOnClickListener {
//            Keranjang.getItemsFromDB(LoggedInUser.loggedInUser.id.toString())
//        }
//
//        binding.tv.setOnClickListener {
//            val list = Keranjang.getTokoArrayList()
//            val list = Keranjang.getProductArrayListPerToko("3")
//            Log.d("pzn shop list",list.toString())
//        }


        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment KeranjangFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            KeranjangFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}