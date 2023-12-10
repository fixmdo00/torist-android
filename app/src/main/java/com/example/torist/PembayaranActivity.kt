package com.example.torist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.example.torist.databinding.ActivityPembayaranBinding
import com.example.torist.`object`.Keranjang
import com.example.torist.`object`.RQ
import com.example.torist.uploadBuktiBayar.UploadRequestBody2
import com.example.torist.uploadFotoProduk.MyAPI
import com.example.torist.uploadFotoProduk.MyAPI2
import com.example.torist.uploadFotoProduk.UploadRequestBody
import com.example.torist.uploadFotoProduk.UploadResponse
import com.example.torist.uploadFotoProduk.getFileName
import com.example.torist.uploadFotoProduk.snackbar
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PembayaranActivity : AppCompatActivity() {

    private var selectedImage: Uri? = null

    private lateinit var binding : ActivityPembayaranBinding
    private lateinit var order_id : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order_id = intent.getStringExtra("order_id")!!

        binding.ivBuktiBayar.setOnClickListener {
            openImagePicker()
        }

        binding.btnUploadBuktiBayar.setOnClickListener {
            uploadImage()
        }


    }

    fun openImagePicker(){
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpg","image/png","image/jpeg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_IMAGE_PICKER)
        }
    }

    private fun uploadImage() {

        if (selectedImage == null){
            binding.root.snackbar("Select an image first")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.loadingProgressBar.visibility = View.VISIBLE
        val body = UploadRequestBody2(file, "image", this)

        MyAPI2().uploadImage(
            MultipartBody.Part.createFormData(
                "image",
                file.name,
                body
            )
        ).enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                Log.d("upload", "error")
                binding.root.snackbar(t.message!!)
                binding.loadingProgressBar.visibility = View.GONE
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                Log.d("upload", response.body().toString())
                response.body()?.let {
                    binding.root.snackbar(it.message)
                    binding.loadingProgressBar.visibility = View.GONE
                    insertToDB(response.body()!!.image, order_id)
                }
//                Log.d("upload", params.toString())
            }
        })

    }

    private fun insertToDB(image: String?, order_id: String) {
        if (image != null){
            val url = "https://torist.my.id/api/update_bukti_bayar.php"
            binding.loadingProgressBar.visibility = View.VISIBLE
            val postData = HashMap<String, String>()
            postData.put("order_id",order_id)
            postData.put("image",image)

            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                com.android.volley.Response.Listener { response ->
                    binding.loadingProgressBar.visibility = View.GONE
                    val resp = response.trim()
                    if (resp == "berhasil"){
                        Toast.makeText(this, "Bukti pembayaran berhasil dikirim", Toast.LENGTH_LONG).show()
                        intent = Intent(this, DetailPesananActivity::class.java)
                        intent.putExtra("id_pesanan", order_id)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal, $resp", Toast.LENGTH_LONG).show()
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    Toast.makeText(this, "eroor", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): Map<String, String> {
                    return postData
                }
            }
            RQ.getRQ().add(stringRequest)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when (requestCode){
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    binding.ivBuktiBayar.setImageURI(selectedImage)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}