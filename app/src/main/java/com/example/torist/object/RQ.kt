package com.example.torist.`object`

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object RQ {
    private lateinit var RQ: RequestQueue
    fun initRQ (context: Context) {

        val requestQueue = Volley.newRequestQueue(context)

        RQ = requestQueue
    }

    fun getRQ(): RequestQueue {
        return RQ
    }
}