package com.example.sampleshoppingcart.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "products") val products: List<Product>,
)

@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "SKU") val skuId: String,
    @Json(name = "ProductName") val productName: String,
    @Json(name = "Description") val desc: String,
    @Json(name = "Price") val price: Double,
    @Json(name = "ImageName") val imageUrl: String,
)
