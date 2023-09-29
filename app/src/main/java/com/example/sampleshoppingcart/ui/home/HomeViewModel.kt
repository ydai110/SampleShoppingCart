package com.example.sampleshoppingcart.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleshoppingcart.data.CartItem
import com.example.sampleshoppingcart.data.Product
import com.example.sampleshoppingcart.data.ProductResponse
import com.example.sampleshoppingcart.util.SharedPreferencesManager
import com.example.sampleshoppingcart.util.Utils
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val _allProductsState = MutableStateFlow<ProductsState>(ProductsState.Loading)

    val allProductsState: StateFlow<ProductsState> = _allProductsState

    fun getProductResponse(context: Context, fileName: String) {
        val jsonString = Utils.getJsonDataFromAsset(context, fileName)

        try {
            val adapter = moshi.adapter(ProductResponse::class.java)
            val response = jsonString?.let { adapter.fromJson(it) }
            _allProductsState.value = ProductsState.Success(response)
        } catch (e: JsonDataException) {
            _allProductsState.value = ProductsState.Error(e.message)
        }
    }

    fun orderProduct(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val orderProductsList = SharedPreferencesManager.getOrderProductsList()
            orderProductsList?.let { it ->
                val productList = it.productList
                if (productList.isEmpty()) {
                    productList.add(CartItem.OrderProduct(product, 1))
                } else {
                    val matchingIndex = productList.indexOfFirst { orderProduct ->
                        orderProduct.product.skuId == product.skuId
                    }
                    if (matchingIndex == -1) {
                        productList.add(CartItem.OrderProduct(product, 1))
                    } else {
                        productList[matchingIndex].quantity += 1
                    }
                }
                SharedPreferencesManager.saveOrderProductsList(orderProductsList)
            }
        }
    }
}

sealed class ProductsState {
    object Loading : ProductsState()

    data class Success(val response: ProductResponse?) : ProductsState()

    data class Error(val errorMsg: String?) : ProductsState()
}
