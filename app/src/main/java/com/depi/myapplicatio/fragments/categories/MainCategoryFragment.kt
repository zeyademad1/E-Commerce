package com.depi.myapplicatio.fragments.categories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.depi.myapplicatio.adapters.BestDealsAdapter
import com.depi.myapplicatio.adapters.BestProductsAdapter
import com.depi.myapplicatio.R
import com.depi.myapplicatio.adapters.SpecialProductsAdapter
import com.depi.myapplicatio.databinding.FragmentMainCategoryBinding
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.util.showBottomNavigationView
import com.depi.myapplicatio.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {

    lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter : SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProducts()

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.bestProducts.collect{
                when (it) {
                    is Resource.Loading<*> -> {
                        showLoading()
                    }
                    is Resource.Success<*> -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error<*> -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.bestProducts.collect{
                when (it) {
                    is Resource.Loading<*> -> {
                        showLoading()
                    }
                    is Resource.Success<*> -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error<*> -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }



        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.bestProducts.collect {
                    when (it) {
                        is Resource.Loading<*> -> {
                            binding.bestProductsProgressbar.visibility = View.VISIBLE
                        }

                        is Resource.Success<*> -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            binding.bestProductsProgressbar.visibility = View.GONE
                        }

                        is Resource.Error<*> -> {
                            Log.e(TAG, it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            binding.bestProductsProgressbar.visibility = View.GONE
                        }

                        else -> Unit
                    }
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchBestProducts()
            }
        })
    }

             private fun setupBestProducts() {
                bestProductsAdapter = BestProductsAdapter()
                binding.rvBestProducts.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                    adapter = bestProductsAdapter
                }
            }

             private fun setupBestDealsRv() {
                bestDealsAdapter = BestDealsAdapter()
                binding.rvBestDealsProducts.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = bestDealsAdapter
                }
            }

             private fun hideLoading() {
                binding.mainCategoryProgressbar.visibility = View.GONE
            }

             private fun showLoading() {
                binding.mainCategoryProgressbar.visibility = View.VISIBLE

            }

             private fun setupSpecialProductsRv() {
                specialProductsAdapter = SpecialProductsAdapter()
                binding.rvSpecialProducts.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = specialProductsAdapter
                }
            }

            override fun onResume() {
                super.onResume()

                showBottomNavigationView()
            }
    }

