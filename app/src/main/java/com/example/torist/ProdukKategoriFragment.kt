package com.example.torist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.ProdukAdapater
import com.example.torist.data.Produk
import com.example.torist.databinding.FragmentBerandaBinding
import com.example.torist.databinding.FragmentProdukKategoriBinding
import com.example.torist.`object`.Products

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProdukKategoriFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentProdukKategoriBinding
    private val binding get() = _binding

    private lateinit var produkAdapater : ProdukAdapater
    private lateinit var recyclerView: RecyclerView
    private var produkArrayList = ArrayList<Produk>()

    var kategori = String()

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
        _binding = FragmentProdukKategoriBinding.inflate(layoutInflater, container, false)

        val bundle = arguments

        if (bundle != null) {
            kategori = bundle.getString("param1","")
        }

        binding.tvKategori.text = kategori

        Products.clearData()
        Products.getFromDB(kategori, binding.loadingProgressBar)
        binding.loadingProgressBar.visibility = View.VISIBLE

        recyclerView = binding.RVProduk
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)

        produkAdapater = ProdukAdapater(produkArrayList)
        recyclerView.adapter = produkAdapater

        Products.liveProducts.observe(viewLifecycleOwner,{
            produkAdapater.list = Products.getArrayList()
            produkAdapater.notifyDataSetChanged()
            produkAdapater.onItemClick = {
                replaceFragment(ProdukDetailFragment.newInstance(it[0],it[1]))
            }
        })

        binding.btnKembali.setOnClickListener {
            requireActivity().onBackPressed()
//            replaceFragment(BerandaFragment())
//            replaceFragment(ProdukDetailFragment())


        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        Products.getFromDB(kategori, binding.loadingProgressBar)
        Products.liveProducts.observe(viewLifecycleOwner,{
            produkAdapater.list = Products.getArrayList()
            produkAdapater.notifyDataSetChanged()
            produkAdapater.onItemClick = {
                replaceFragment(ProdukDetailFragment.newInstance(it[0],it[1]))
            }
        })

    }

    override fun onStop() {
        super.onStop()
        Products.clearData()
    }

    fun replaceFragment (fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProdukKategoriFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}