package com.example.torist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.torist.databinding.ActivityTokoTambahProdukBinding
import com.example.torist.`object`.LoggedInUser
import com.example.torist.`object`.RQ
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
import kotlin.random.Random

class TokoTambahProdukActivity : AppCompatActivity() {

    lateinit var binding : ActivityTokoTambahProdukBinding

    private var selectedImage: Uri? = null

    val params = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokoTambahProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBuktiBayar.setOnClickListener {
            openImagePicker()
        }

        binding.btnTambahProduk.setOnClickListener {
            if (binding.itNamaProduk.text contentEquals "" ||
                binding.itHargaProduk.text contentEquals "" ||
                binding.itDeskProduk.text contentEquals ""
            ){
                Toast.makeText(this,"Data tidak lengkap", Toast.LENGTH_LONG).show()
            } else {
                val radioId = binding.radioGroup.checkedRadioButtonId
                val radio : RadioButton = findViewById(radioId)
                params.put("kategori",radio.text)
                params.put("id_toko",LoggedInUser.idToko)
                params.put("nama", binding.itNamaProduk.text)
                params.put("harga", binding.itHargaProduk.text)
                params.put("desk", binding.itDeskProduk.text)
                uploadImage()
            }
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
        params.put("id", getNewId())

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
        val body = UploadRequestBody(file, "image", this)

        MyAPI().uploadImage(
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
                    params.put("url", response.body()!!.image)
                    insertToDB(params)
                }
                Log.d("upload", params.toString())

            }
        })

    }
    fun insertToDB (params : JSONObject) {
        binding.loadingProgressBar.visibility = View.VISIBLE

        val url = "https://torist.my.id/api/add_new_product.php"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,url,params,
            {response ->
                binding.loadingProgressBar.visibility = View.GONE
                val status = response.getString("status")
                if (status == "berhasil"){
                    Toast.makeText(this, "Produk Berhasil Ditambahkan", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, TokoProdukActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Upload berhasil, DB Gagal", Toast.LENGTH_LONG).show()
                }
            },
            {error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            })
        RQ.getRQ().add(jsonObjectRequest)
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
    fun getNewId() : Int {
        val randomInt = Random.nextInt(100000, 1000000)
        return randomInt
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}