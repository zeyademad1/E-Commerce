package com.depi.myapplicatio.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * To add extra spaces between recycler view items
 * It just act like a margin
 */

class VerticalItemDecoration (private val amount : Int =30) : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }
}