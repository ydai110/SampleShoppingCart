package com.example.sampleshoppingcart.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleshoppingcart.data.CartItem
import com.example.sampleshoppingcart.databinding.FragmentCartBinding
import com.example.sampleshoppingcart.ui.ProductItemDecoration
import com.example.sampleshoppingcart.ui.adapter.CartCarouselAdapter
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    private val binding get() = _binding!!

    private lateinit var cartViewModel: CartViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var cartCarouselAdapter: CartCarouselAdapter

    private lateinit var cartTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cartViewModel =
            ViewModelProvider(this)[CartViewModel::class.java]

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cartTitle = binding.cartTitle

        setUpCartViewModel()
        initRecyclerView()
        return root
    }

    private fun initRecyclerView() {
        cartCarouselAdapter = CartCarouselAdapter(
            mutableListOf(),
            object : CartCarouselAdapter.OnProductClickListener {
                override fun onRemove(position: Int) {
                    cartViewModel.removeOrderProduct(position)
                }

                override fun onChangeItemQuantity(position: Int, size: Int) {
                    cartViewModel.changeOrderProductSize(size = size, pos = position)
                }
            })
        val itemDecoration = ProductItemDecoration(requireContext())
        recyclerView = binding.recyclerView
        recyclerView.apply {
            adapter = cartCarouselAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setUpCartViewModel() {
        cartViewModel.getOrderProductResponse()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.allOrderProductsState.collect { productsState ->
                    when (productsState) {
                        is OrderProductsState.Loading -> {
                            // Add loading animation
                        }
                        is OrderProductsState.Error -> {
                            // Add error handling, like a snackbar or toast.
                            // Internet issue, let user to open network/try again later
                        }
                        is OrderProductsState.Success -> {
                            val response = productsState.response
                            response?.let {
                                handleSuccessResponse(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleSuccessResponse(response: List<CartItem>) {
        // Set data for recyclerview.
        cartCarouselAdapter.setData(response)
        if (response.isEmpty()) {
            cartTitle.text = "Your Cart is Empty."
        } else {
            cartTitle.text = "Your Cart Items are here."
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
