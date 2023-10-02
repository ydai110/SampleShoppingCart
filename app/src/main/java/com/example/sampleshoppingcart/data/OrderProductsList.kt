package com.example.sampleshoppingcart.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderProductsList(
    @Json(name = "productsList")
    val productList: MutableList<OrderProduct>,
)

sealed class CartItem

@JsonClass(generateAdapter = true)
data class OrderProduct(
    @Json(name = "productInfo")
    val product: Product,
    @Json(name = "quantity")
    var quantity: Int,
    var discountPrice: Double = product.price,
) : CartItem() {

    // Used for applying coupon/ discount price
    // Eg, 10% for each item you buy

    // For buy one get one, need to set strategy
    // For buy one get 10-20% off for next product, need to set COR pattern. Like Handler.
    fun adjustPrice(amount: Double) {
        discountPrice += amount
    }

    fun calculateDiscountPrice(quantity: Int): Double {
        return discountPrice * quantity
    }

    fun calculateTotalPrice(quantity: Int): Double {
        return product.price * quantity
    }
}

data class SummaryItem(
    val totalPrice: Double,
    val discountPrice: Double,
    val taxFee: Double,
    val estimatedTotalPrice: Double,
) : CartItem()
