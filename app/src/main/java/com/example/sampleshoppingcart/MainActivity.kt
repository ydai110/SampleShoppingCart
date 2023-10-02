package com.example.sampleshoppingcart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sampleshoppingcart.databinding.ActivityMainBinding
import com.example.sampleshoppingcart.ui.cart.CartViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cartBadgeInNav: BadgeDrawable

    private var cartBadgeInToolbar: BadgeDrawable? = null

    private lateinit var navView: BottomNavigationView

    private val cartViewModel: CartViewModel by viewModels()

    private var orderProductsNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigate_cart, R.id.navigate_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        initBadges()
        initViewModel()
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.allOrderProductsNum.collect { num ->
                    if (num == 0) {
                        cartBadgeInNav.isVisible = false
                    } else {
                        cartBadgeInNav.isVisible = true
                        cartBadgeInNav.number = num
                    }
                    orderProductsNum = num
                }
            }
        }
        cartViewModel.getOrderProductResponse()
    }

    private fun initBadges() {
        cartBadgeInNav = navView.getOrCreateBadge(R.id.navigate_cart)
    }

    @androidx.annotation.OptIn(com.google.android.material.badge.ExperimentalBadgeUtils::class)
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (cartBadgeInToolbar == null) cartBadgeInToolbar = BadgeDrawable.create(this)
        if (orderProductsNum == 0) {
            cartBadgeInToolbar?.isVisible = false
        } else {
            cartBadgeInToolbar?.isVisible = true
            cartBadgeInToolbar?.number = orderProductsNum
        }
        BadgeUtils.attachBadgeDrawable(cartBadgeInToolbar!!, binding.toolbar, R.id.navigate_cart)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu_nav, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment_activity_main))
                || super.onOptionsItemSelected(item)
    }
}
