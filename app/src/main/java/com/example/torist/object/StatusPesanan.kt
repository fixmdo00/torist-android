package com.example.torist.`object`

import android.view.View
import android.widget.LinearLayout

object StatusPesanan {
    fun getTokoStatus(id : String, containerForm : LinearLayout, containerTv : LinearLayout) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Menunggu Pembayaran"
                containerForm.visibility = View.GONE
                containerTv.visibility = View.GONE
            }
            "2" -> {
                status = "Pembayaran Perlu Dikonfirmasi"

                containerForm.visibility = View.GONE
                containerTv.visibility = View.GONE
            }
            "3" -> {
                status = "Perlu Dikirim"
                containerForm.visibility = View.VISIBLE
                containerTv.visibility = View.GONE
            }
            "4" -> {
                status = "Dalam Pengiriman"
                containerForm.visibility = View.GONE
                containerTv.visibility = View.VISIBLE
            }
            "5" -> {
                status = "Selesai"
                containerForm.visibility = View.GONE
                containerTv.visibility = View.VISIBLE
            }
        }
        return status
    }

    fun getTokoStatus(id : String) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Menunggu Pembayaran"
            }
            "2" -> {
                status = "Pembayaran Perlu Dikonfirmasi"
            }
            "3" -> {
                status = "Perlu Dikirim"
            }
            "4" -> {
                status = "Dalam Pengiriman"
            }
            "5" -> {
                status = "Selesai"
            }
        }
        return status
    }

    fun getStatus(id : String) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Belum Dibayar"
            }
            "2" -> {
                status = "Menunggu Konfirmasi Pembayaran"
            }
            "3" -> {
                status = "Menunggu Pengiriman"
            }
            "4" -> {
                status = "Dalam Pengiriman"
            }
            "5" -> {
                status = "Selesai"
            }
        }
        return status
    }

    fun getStatus(id : String, containerTv : LinearLayout, containerbtn : LinearLayout) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Belum Dibayar"
                containerTv.visibility = View.GONE
                containerbtn.visibility = View.GONE
            }
            "2" -> {
                status = "Menunggu Konfirmasi Pembayaran"
                containerTv.visibility = View.GONE
                containerbtn.visibility = View.GONE
            }
            "3" -> {
                status = "Menunggu Pengiriman"
                containerTv.visibility = View.GONE
                containerbtn.visibility = View.GONE
            }
            "4" -> {
                status = "Dalam Pengiriman"
                containerTv.visibility = View.VISIBLE
                containerbtn.visibility = View.VISIBLE
            }
            "5" -> {
                status = "Selesai"
                containerTv.visibility = View.VISIBLE
                containerbtn.visibility = View.GONE
            }
        }
        return status
    }
}