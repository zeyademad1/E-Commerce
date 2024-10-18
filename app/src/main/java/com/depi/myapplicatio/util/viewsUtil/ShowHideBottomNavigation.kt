package com.depi.myapplicatio.util.viewsUtil

import androidx.fragment.app.Fragment
import com.depi.myapplicatio.ui.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.depi.myapplicatio.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.depi.myapplicatio.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}