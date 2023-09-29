package com.example.sampleshoppingcart.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleshoppingcart.R
import com.example.sampleshoppingcart.data.Product
import com.example.sampleshoppingcart.data.ProductResponse
import com.example.sampleshoppingcart.databinding.FragmentHomeBinding
import com.example.sampleshoppingcart.ui.ProductItemDecoration
import com.example.sampleshoppingcart.ui.adapter.ProductCarouselAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var productsCarouselAdapter: ProductCarouselAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        setUpHomeViewModel()

        return root
    }

    private fun initRecyclerView() {
        productsCarouselAdapter = ProductCarouselAdapter(
            mutableListOf(),
            object : ProductCarouselAdapter.OnProductOrderListener {
                override fun onClick(product: Product) {
                    homeViewModel.orderProduct(product)
                    findNavController().navigate(R.id.navigate_cart)
                }
            }
        )
        val itemDecoration = ProductItemDecoration(requireContext())
        recyclerView = binding.recyclerView
        recyclerView.apply {
            adapter = productsCarouselAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setUpHomeViewModel() {
        homeViewModel.getProductResponse(requireContext(), "mockData.json")

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.allProductsState.collect { productsState ->
                    when (productsState) {
                        is ProductsState.Loading -> {
                            // Add loading animation
                        }
                        is ProductsState.Error -> {
                            // Add error handling, like a snackbar or toast.
                            // Internet issue, let user to open network/try again later
                        }
                        is ProductsState.Success -> {
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

    private fun handleSuccessResponse(response: ProductResponse) {
        // Set data for recyclerview.
        productsCarouselAdapter.setData(response.products)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
