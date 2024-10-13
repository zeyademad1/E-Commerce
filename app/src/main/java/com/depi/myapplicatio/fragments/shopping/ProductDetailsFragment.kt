package com.depi.myapplicatio.fragments.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.depi.myapplicatio.R
import com.depi.myapplicatio.activites.ShoppingActivity
import com.depi.myapplicatio.adapters.ColorsAdapter
import com.depi.myapplicatio.adapters.SizesAdapter
import com.depi.myapplicatio.adapters.ViewPager2Images
import com.depi.myapplicatio.data.CartProduct
import com.depi.myapplicatio.databinding.FragmentProductDetailsBinding
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.util.hideBottomNavigationView
import com.depi.myapplicatio.viewmodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private var selectedColor : Int? = null
    private var selectedSize : String? = null
    private val detailsViewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //To hide BottomNavigation when we are here in this fragment (ProductDetailsFragment)
        hideBottomNavigationView()

        _binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(_binding == null)
            return
        val product = args.product

        setUpSizesRv()
        setUpColorsRv()
        setUpViewpager()

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize =it
        }
        colorsAdapter.onItemClick = {
            selectedColor =it
        }

        binding.buttonAddToCart.setOnClickListener {
            detailsViewModel.addUpdateProductToCart(CartProduct(product,1,selectedColor,selectedSize))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                detailsViewModel.addToCart.collect{
                    when(it){
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(requireContext(),"Product was added",Toast.LENGTH_LONG).show()
                        }

                        is Resource.Error -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }
        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description

            //if this item doesn't has colors
            if(product.colors.isNullOrEmpty()){
                rvColors.visibility = View.INVISIBLE
            }
            //if this item doesn't has sizes
            if(product.sizes.isNullOrEmpty()){
                rvSizes.visibility = View.INVISIBLE
            }
        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }

    }

    private fun setUpViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setUpColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setUpSizesRv() {
        binding.rvSizes.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}