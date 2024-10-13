package com.depi.myapplicatio.data

data class CartProduct (
    val product : Product,
    val quantity : Int,
    val selectedColor : Int? = null,
    val selectedSize : String? = null
){
    constructor() : this(Product(),0,null,null)
}