package com.depi.myapplicatio.ui.fragments.shopping

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.depi.myapplicatio.R
import com.depi.myapplicatio.adapters.recyclersAdapters.shoppingAdapters.AddressAdapter
import com.depi.myapplicatio.adapters.recyclersAdapters.shoppingAdapters.BillingProductsAdapter
import com.depi.myapplicatio.data.models.Address
import com.depi.myapplicatio.data.models.CartProduct
import com.depi.myapplicatio.data.models.order.Order
import com.depi.myapplicatio.data.models.order.OrderStatus
import com.depi.myapplicatio.databinding.FragmentBillingBinding
import com.depi.myapplicatio.util.recyclerDecoration.HorizontalDecoration
import com.depi.myapplicatio.ui.state.Resource
import com.depi.myapplicatio.util.viewsUtil.showDialogue
import com.depi.myapplicatio.ui.viewmodels.shopping.BillingViewModel
import com.depi.myapplicatio.ui.viewmodels.settings.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private var selectedAddress: Address? = null

    private var productsList = emptyList<CartProduct>()
    private var totalPrice = 0f

    /**
     * TODO: Missing pass data in navigation from cart to billing to show in the recycler
     * */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsList = args.products.toList()
        totalPrice = args.totalPrice
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBillingRv()
        setupAddressRv()

        // TODO: Collect Address Details
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
                            Toast.makeText( // if rotate it will show toast again , so we should use sharedFlow
                                requireContext(), "Error ${result.message}", Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> Unit
                    }
                }
            }
        }


        // TODO: Show Loading Progress for Ordering
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                orderViewModel.order.collectLatest { state ->
                    when (state) {
                        is Resource.Loading -> binding.buttonPlaceOrder.startAnimation()
                        is Resource.Success -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            findNavController().navigateUp()
                            Snackbar.make(
                                requireView(), "Ordered Successfully", Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Error -> {
                            lifecycleScope.launch {
                                orderViewModel.errorState.collectLatest {
                                    Toast.makeText(
                                        requireContext(),
                                        it,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        else -> Unit

                    }
                }
            }
        }


        // TODO: Add New Address
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }


        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Snackbar.make(
                    requireView(), "Please Select Your Shipping Address", Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            showPlaceOrderConfirmDialogue()
        }




        billingAdapter.differ.submitList(productsList)
        binding.tvTotalPrice.text = "$ $totalPrice"

    }

    private fun showPlaceOrderConfirmDialogue() {
        showDialogue(context = requireContext(),
            title = "Confirm Order Items",
            message = "Do You Want to Confirm these orders",
            alertIcon = R.drawable.ic_info,
            negativeButtonText = "Cancel",
            positiveButtonText = "Order",
            isCancelable = true,
            onNegativeAction = { dialog ->
                // Handle negative action (optional)
                dialog.dismiss()
            },
            onPositiveAction = { dialog ->
                // TODO: Place Order
                orderViewModel.placeOrder(
                    Order(
                        orderStatus = OrderStatus.Ordered.status,
                        totalPrice = totalPrice,
                        address = selectedAddress!!,
                        products = productsList
                    )
                )
                dialog.dismiss()

            })

    }


    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            addItemDecoration(HorizontalDecoration())
            adapter = addressAdapter
            addressAdapter.onClick = { address ->
                selectedAddress = address
            }
        }
    }

    private fun setupBillingRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )
            addItemDecoration(HorizontalDecoration())
            adapter = billingAdapter
        }
    }
}
