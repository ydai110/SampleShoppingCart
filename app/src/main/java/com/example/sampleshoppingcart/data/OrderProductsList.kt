package com.example.sampleshoppingcart.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderProductsList(
    @Json(name = "productsList")
    val productList: MutableList<CartItem.OrderProduct>,
)

sealed class CartItem {
    @JsonClass(generateAdapter = true)
    data class OrderProduct(
        @Json(name = "productInfo")
        val product: Product,
        @Json(name = "quantity")
        var quantity: Int,
    ) : CartItem()

    data class SummaryItem(
        val totalPrice: Double,
        val discountPrice: Double,
        val taxFee: Double,
        val estimatedTotalPrice: Double,
    ) : CartItem()
}
