package com.depi.myapplicatio.firebase

import com.depi.myapplicatio.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class FirebaseCommon (
    private val firebase : FirebaseFirestore,
    private val auth : FirebaseAuth
) {
    //to provide cart collection
    private val cartCollection = firebase.collection("user").document(auth.uid!!).collection("cart")

    //save cartProduct into cart collection (new product)
    fun addProductToCart(cartProduct : CartProduct,onResult : (CartProduct?,Exception?) ->Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }
            .addOnFailureListener {
                onResult(null,it)
            }
    }

    fun increaseQuantity(documentId : String,onResult : (String?,Exception?) -> Unit){
        firebase.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let{
                val newQuantity = productObject.quantity + 1
                val newProductObject = productObject.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }



    fun decreaseQuantity(documentId : String,onResult : (String?,Exception?) -> Unit){
        firebase.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let{
                val newQuantity = productObject.quantity - 1
                val newProductObject = productObject.copy(quantity = newQuantity)
                transaction.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    enum class QuantityChanging{
        Increase, Decrease
    }
}