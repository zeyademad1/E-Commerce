package com.depi.myapplicatio.data

import android.os.Parcelable
import com.depi.myapplication.data.Product
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
): Parcelable {
    constructor() : this(Product(), 1, null, null)
}
