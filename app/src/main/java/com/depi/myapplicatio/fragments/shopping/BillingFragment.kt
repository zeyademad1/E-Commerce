package com.depi.myapplicatio.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.depi.myapplicatio.adapters.AddressAdapter

import com.depi.myapplicatio.adapters.BillingProductsAdapter
import com.depi.myapplicatio.databinding.FragmentBillingBinding
import com.depi.myapplicatio.util.HorizontalDecoration
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.viewmodel.BillingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()


    /**
     * TODO: Missing pass data in navigation from cart to billing to show in the recycler
     * */



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingRv()
        setupAddressRv()

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                billingViewModel.address.collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> binding.progressbarAddress.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.GONE
                            addressAdapter.differ.submitList(result.data)
                        }

                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Error ${result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            addItemDecoration(HorizontalDecoration())

            adapter = addressAdapter
        }
    }

    private fun setupBillingRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            addItemDecoration(HorizontalDecoration())
            adapter = billingAdapter
        }
    }
}
