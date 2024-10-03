package com.depi.myapplicatio.fragments.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.depi.myapplicatio.R
import com.depi.myapplicatio.adapters.HomeViewpagerAdapter
import com.depi.myapplicatio.databinding.FragmentHomeBinding
import com.depi.myapplicatio.fragments.categories.AccessoryFragment
import com.depi.myapplicatio.fragments.categories.ChairFragment
import com.depi.myapplicatio.fragments.categories.CupboardFragment
import com.depi.myapplicatio.fragments.categories.FurnitureFragment
import com.depi.myapplicatio.fragments.categories.MainCategoryFragment
import com.depi.myapplicatio.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment(),
        )

        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome){tab, position ->
            when(position){
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
                else -> tab.text = "tab $position"
            }
        }.attach()
    }
}