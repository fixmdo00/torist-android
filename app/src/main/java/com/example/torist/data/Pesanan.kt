package com.example.torist.data

data class Pesanan(
    val order_id : String,
    val order_time : String,
    val order_status : String,
    val order_paymemt_type : String,
    val order_payment_photo : String,
    val order_name : String,
    val order_telp : String,
    val order_address : String,
    val order_note : String,
    val order_products_qty : Int,
    val shop_name : String,
    val shop_address : String,
    val order_total_price : Int,
    val order_delivery_price : Int,
    val order_courier : String,
    val order_resi : String,
    val shop_bank : String,
    val shop_bank_account : String
)
