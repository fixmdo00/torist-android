package com.example.torist.`object`

import android.content.Context
import android.content.SharedPreferences

object SP {

    lateinit var LoginSP : SharedPreferences

    fun initLoginSP (context: Context){
        val sp = context.getSharedPreferences("loginSP", Context.MODE_PRIVATE)
        LoginSP = sp
    }

    fun getSP () : SharedPreferences {
        return LoginSP
    }

    fun getLoginStatus () : Boolean {
        return LoginSP.getBoolean("loginStatus", false)
    }

    fun hapusSP (){
        LoginSP.edit().putBoolean("loginStatus", false).apply()
        LoginSP.edit().putString("userId", null).apply()
    }

}