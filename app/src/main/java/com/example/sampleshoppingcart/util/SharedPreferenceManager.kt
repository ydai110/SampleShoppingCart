package com.example.sampleshoppingcart.util

import android.content.Context
import android.content.SharedPreferences
import com.example.sampleshoppingcart.data.OrderProductsList
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object SharedPreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var jsondapter: JsonAdapter<OrderProductsList>
    private lateinit var moshi: Moshi

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        jsondapter = moshi.adapter(OrderProductsList::class.java)
    }

    fun saveOrderProductsList(orderProductsList: OrderProductsList) {
        val userJson = jsondapter.toJson(orderProductsList)
        sharedPreferences.edit().putString("orderProductsList", userJson).apply()
    }

    fun getOrderProductsList(): OrderProductsList? {
        val userJson = sharedPreferences.getString("orderProductsList", null)
        return if (userJson != null) {
            jsondapter.fromJson(userJson)
        } else {
            OrderProductsList(mutableListOf())
        }
    }
}
