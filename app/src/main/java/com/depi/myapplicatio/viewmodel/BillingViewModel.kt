package com.depi.myapplicatio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplicatio.data.Address
import com.depi.myapplicatio.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    val fireStore: FirebaseFirestore,
    val auth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddress()
    }

    private fun getUserAddress() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }
        fireStore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                }
                val address = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(address!!)) }

            }
    }
}