package com.example.torist.`object`

import android.view.View
import android.widget.Button
import android.widget.LinearLayout

object StatusPembayaran {
    fun getStatus(id : String, button: Button, container : LinearLayout) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Menunggu Pembayaran"
                container.visibility = View.VISIBLE
            }
            "2" -> {
                status = "Menunggu Konfirmasi Pembayaran"
                button.hint = "Lihat Bukti Bayar"
            }
            "3","4","5"-> {
                status = "Berhasil"
                button.hint = "Lihat Bukti Bayar"
            }
        }
        return status
    }

    fun getTokoStatus(id : String, button1: Button, button2 : Button) : String{
        var status = ""
        when (id){
            "1" -> {
                status = "Belum Dibayar"
                button1.visibility = View.GONE
            }
            "2" -> {
                status = "Pembayaran Perlu Dikonfirmasi"
                button1.hint = "Lihat Bukti Bayar"
                button1.visibility = View.VISIBLE
                button2.visibility = View.VISIBLE
            }
            "3","4","5"-> {
                status = "Pembayaran Telah Dikonfirmasi"
                button1.hint = "Lihat Bukti Bayar"
                button1.visibility = View.VISIBLE
                button2.visibility = View.GONE
            }
        }
        return status
    }
}