package com.depi.myapplicatio.ui.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.depi.myapplicatio.R
import com.depi.myapplicatio.adapters.recyclersAdapters.shoppingAdapters.CartProductAdapter
import com.depi.myapplicatio.data.firebase.FirebaseCommon
import com.depi.myapplicatio.databinding.FragmentCartBinding
import com.depi.myapplicatio.ui.state.Resource
import com.depi.myapplicatio.ui.viewmodels.CartViewModel
import com.depi.myapplicatio.util.recyclerDecoration.VerticalItemDecoration
import com.depi.myapplicatio.util.viewsUtil.showDialogue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    private var totalPrice = 0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.productsPrice.collectLatest { _price ->
                    _price?.let {
                        totalPrice = it
                        binding.tvTotalPrice.text = "$ $_price"
                    }

                }
            }
        }
        return _binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (_binding == null)
            return
        setUpCartRv()

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }


        binding.btnCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment2(
                totalPrice,
                cartAdapter.differ.currentList.toTypedArray(),
                false
            )
            findNavController().navigate(action)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteDialog.collectLatest {
                    showDialogue(context = requireContext(),
                        title = "Delete item from cart",
                        message = "Do you want to delete this item from your cart?",
                        alertIcon = R.drawable.ic_info,
                        negativeButtonText = "Cancel",
                        positiveButtonText = "Delete",
                        isCancelable = true,
                        onNegativeAction = { dialog ->
                            // Handle negative action (optional)
                            dialog.dismiss()
                        },
                        onPositiveAction = { dialog ->
                            // TODO: Delete Product
                            viewModel.deleteCartProduct(it)
                            dialog.dismiss()

                        })
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsPrice.collectLatest { price ->
                    price?.let {
                        binding.tvTotalPrice.text = "$ ${price}"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarCart.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            if (it.data!!.isEmpty()) {
                                showEmptyCart()
                                hideOtherViews()
                            } else {
                                hindEmptyList()
                                showOtherViews()
                                cartAdapter.differ.submitList(it.data)
                            }
                        }

                        is Resource.Error -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            btnCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            btnCheckout.visibility = View.GONE
        }
    }

    private fun setUpCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }

    }

    private fun hindEmptyList() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }

    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}