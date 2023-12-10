package com.example.torist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.ProdukAdapater
import com.example.torist.data.Produk
import com.example.torist.databinding.FragmentBerandaBinding
import com.example.torist.`object`.Products

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BerandaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BerandaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit  var _binding: FragmentBerandaBinding
    private val binding get() = _binding

    private lateinit var produkAdapater : ProdukAdapater
    private lateinit var recyclerView: RecyclerView
    private var produkArrayList = ArrayList<Produk>()


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
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)

        if (Products._products.length() == 0) {
            Products.getFromDB(binding.loadingProgressBar)
        }

        recyclerView = binding.RVproduk
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

        binding.btnBungaHias.setOnClickListener {
            replaceFragment(ProdukKategoriFragment.newInstance("Bunga Hias",""))
        }

        binding.btnBungaDuka.setOnClickListener {
            replaceFragment(ProdukKategoriFragment.newInstance("Bunga Bouquet",""))
        }

        binding.btnBungaPlastik.setOnClickListener {
            replaceFragment(ProdukKategoriFragment.newInstance("Bunga Papan",""))
        }

        binding.btnBungaSegar.setOnClickListener {
            replaceFragment(ProdukKategoriFragment.newInstance("Bunga Duka",""))
        }


        return binding.root
    }

    fun replaceFragment (fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BerandaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BerandaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}