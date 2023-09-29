package com.example.sampleshoppingcart.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleshoppingcart.data.CartItem
import com.example.sampleshoppingcart.data.OrderProductsList
import com.example.sampleshoppingcart.util.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val _allOrderProductsState =
        MutableStateFlow<OrderProductsState>(OrderProductsState.Loading)

    val allOrderProductsState: StateFlow<OrderProductsState> = _allOrderProductsState

    private var cache: MutableList<CartItem> = mutableListOf()

    fun getOrderProductResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            val orderProductsList = SharedPreferencesManager.getOrderProductsList()
            orderProductsList?.let {
                if (it.productList.isEmpty()) {
                    _allOrderProductsState.value = OrderProductsState.Success(mutableListOf())
                } else {
                    val cartItemList = calculateTotalPrice(it.productList)
                    _allOrderProductsState.value =
                        OrderProductsState.Success(cartItemList)
                    cache.clear()
                    cache.addAll(cartItemList)
                }
            } ?: run {
                _allOrderProductsState.value =
                    OrderProductsState.Error("There is something wrong with stored data")
            }
        }
    }

    private fun calculateTotalPrice(orderProducts: List<CartItem.OrderProduct>): List<CartItem> {
        var totalPrice = 0.0
        orderProducts.forEach { item ->
            totalPrice += item.product.price * item.quantity
        }

        val summaryItem = CartItem.SummaryItem(
            totalPrice = totalPrice,
            taxFee = 0.0,
            estimatedTotalPrice = totalPrice,
            discountPrice = 0.0
        )

        val cartItemList = mutableListOf<CartItem>()
        cartItemList.addAll(orderProducts)
        cartItemList.add(summaryItem)
        return cartItemList
    }

    fun changeOrderProductSize(size: Int, pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderProduct = (cache[pos] as CartItem.OrderProduct)
            val prevUnit = orderProduct.quantity
            calculatePrice(size - prevUnit, orderProduct.product.price)
            orderProduct.quantity = size

            val orderProducts = cache.subList(0, cache.size - 1).map {
                it as CartItem.OrderProduct
            }.toMutableList()

            SharedPreferencesManager.saveOrderProductsList(OrderProductsList(orderProducts))
            val newPost = mutableListOf<CartItem>()
            newPost.addAll(cache)
            _allOrderProductsState.value = OrderProductsState.Success(newPost)
        }
    }

    fun removeOrderProduct(pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val orderProduct = cache[pos] as CartItem.OrderProduct
            calculatePrice(0 - orderProduct.quantity, orderProduct.product.price)
            cache.removeAt(pos)
            val orderProducts = cache.subList(0, cache.size - 1).map {
                it as CartItem.OrderProduct
            }.toMutableList()

            SharedPreferencesManager.saveOrderProductsList(OrderProductsList(orderProducts))
            if (cache.size == 1) {
                cache.clear()
            }

            _allOrderProductsState.value = OrderProductsState.Success(cache)
        }
    }

    private fun calculatePrice(factor: Int, unitPrice: Double) {
        val summaryItem = cache[cache.size - 1] as CartItem.SummaryItem
        val totalPrice = summaryItem.totalPrice + factor * unitPrice
        val newSummaryItem = CartItem.SummaryItem(
            totalPrice = totalPrice,
            taxFee = 0.0,
            estimatedTotalPrice = totalPrice,
            discountPrice = 0.0
        )
        cache[cache.size - 1] = newSummaryItem
    }
}

sealed class OrderProductsState {
    object Loading : OrderProductsState()

    data class Success(val response: List<CartItem>?) : OrderProductsState()

    data class Error(val errorMsg: String?) : OrderProductsState()
}
