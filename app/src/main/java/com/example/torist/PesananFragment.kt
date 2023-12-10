package com.example.torist

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.torist.adapter.PesananAdapater
import com.example.torist.data.Pesanan
import com.example.torist.databinding.FragmentKeranjangBinding
import com.example.torist.databinding.FragmentPesananBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PesananFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PesananFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _binding : FragmentPesananBinding
    private val binding get() = _binding

    private lateinit var pesananAdapter : PesananAdapater
    private lateinit var recyclerView: RecyclerView
    private var pesananList = ArrayList<Pesanan>()

    private val pesanan =    com.example.torist.`object`.Pesanan

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
        _binding = FragmentPesananBinding.inflate(layoutInflater, container, false)

        pesanan.getFromDB(binding.loadingProgressBar)

        recyclerView = binding.RVPesanan
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        pesananAdapter = PesananAdapater(pesananList,this,binding.loadingProgressBar,requireContext())
        recyclerView.adapter = pesananAdapter

        pesanan.livePesanan.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = false
            pesananAdapter.list = pesanan.getPesananArrayList()
            pesananAdapter.notifyDataSetChanged()
            if(it.length() == 0){
                binding.tvPesananKosong.visibility = View.VISIBLE
            } else{
                binding.tvPesananKosong.visibility = View.INVISIBLE
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            pesanan.getFromDB(binding.loadingProgressBar)
        }

        binding.btnChat.setOnClickListener {
            val intent = Intent(context, ChatListActivity::class.java)
            intent.putExtra("userType","user")
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        pesanan.getFromDB(binding.loadingProgressBar)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PesananFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PesananFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}