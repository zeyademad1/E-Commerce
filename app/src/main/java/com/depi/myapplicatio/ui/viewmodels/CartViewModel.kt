package com.depi.myapplicatio.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplicatio.data.CartProduct
import com.depi.myapplicatio.data.firebase.FirebaseCommon
import com.depi.myapplicatio.util.priceHelper.getProductPrice
import com.depi.myapplicatio.ui.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

    @HiltViewModel
    class CartViewModel @Inject constructor(
        private val firestore : FirebaseFirestore,
        private val auth : FirebaseAuth,
        private val firebaseCommon: FirebaseCommon
    ) : ViewModel(){

        private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
        val cartProducts = _cartProducts.asStateFlow()


        val productsPrice = cartProducts.map {
            when(it){
                is Resource.Success -> {
                    calculatePrice(it.data!!)
                }
                else -> null
            }
        }

        //shared flow to show a dialog to user if wanted to delete this item from cart
        private val _deleteDialog = MutableSharedFlow<CartProduct>()
        val deleteDialog = _deleteDialog.asSharedFlow()

        private var cartProductDocuments = emptyList<DocumentSnapshot>()

        fun deleteCartProduct(cartProduct  : CartProduct){
            val index = cartProducts.value.data?.indexOf(cartProduct)
            if(index != null && index != -1) {
                val documentId = cartProductDocuments[index].id
                firestore.collection("user").document(auth.uid!!).collection("cart")
                    .document(documentId).delete()
            }
        }



        private fun calculatePrice(data: List<CartProduct>): Float {
            return data.sumOf { cartProduct ->
                (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
            }.toFloat()
        }


        init {
            getCartProduct()
        }

        private fun getCartProduct(){

            viewModelScope.launch {
                _cartProducts.emit(Resource.Loading())
            }

            firestore.collection("user").document(auth.uid!!).collection("cart")
                .addSnapshotListener { value, error ->
                    if(error != null || value == null){
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Error(error?.message.toString()))
                        }
                    }else{
                        cartProductDocuments = value.documents
                        val cartProducts = value.toObjects(CartProduct::class.java)
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Success(cartProducts))
                        }
                    }
                }
        }


        fun changeQuantity(
            cartProduct: CartProduct,
            quantityChanging: FirebaseCommon.QuantityChanging
        ) {

            /**
             * the index could be equal to -1 if the function [getCartProduct] delays which will also delay the result we expect to be inside the [_cartProducts]
             * and to prevent the app from crashing we need a check
             */
            val index = cartProducts.value.data?.indexOf(cartProduct)
            if(index != null && index != -1){
                val documentId = cartProductDocuments[index].id

                when(quantityChanging){
                    FirebaseCommon.QuantityChanging.INCREASE -> {
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Loading())
                        }
                        increaseQuantity(documentId)
                    }
                    FirebaseCommon.QuantityChanging.DECREASE -> {
                        if(cartProduct.quantity ==  1) {
                            viewModelScope.launch {
                                _deleteDialog.emit(cartProduct)
                            }
                            return
                        }
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Loading())
                        }
                        decreaseQuantity(documentId)
                    }

                    else -> Unit
                }
            }


        }

        private fun decreaseQuantity(documentId: String) {
            firebaseCommon.decreaseQuantity(documentId){ _ , exception ->
                if(exception != null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(exception.message.toString()))
                    }
                }
            }
        }

        private fun increaseQuantity(documentId: String) {
            firebaseCommon.increaseQuantity(documentId){ _ , exception ->
                if(exception != null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(exception.message.toString()))
                    }
                }
            }
        }


}