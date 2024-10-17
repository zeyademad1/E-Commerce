package com.depi.myapplicatio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.depi.myapplicatio.R
import com.depi.myapplicatio.databinding.ActivityShoppingBinding
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val binding by lazy{
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.shoppingHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigation)
            .setupWithNavController(navController)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.cartProducts.collectLatest {
                    when(it){
                        is Resource.Success -> {
                            val count = it.data?.size ?: 0
                            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                            bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                number = count
                                backgroundColor = resources.getColor(R.color.g_blue)
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }



    }
}