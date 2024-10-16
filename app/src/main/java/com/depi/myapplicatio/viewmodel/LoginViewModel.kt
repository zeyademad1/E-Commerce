package com.depi.myapplicatio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.myapplicatio.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _login = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { user ->
                        viewModelScope.launch {
                            _login.emit(Resource.Success(user))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    viewModelScope.launch {
                        _login.emit(Resource.Error(exception.message.toString()))
                    }
                }

    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            viewModelScope.launch {
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _login.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())

            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _resetPassword.emit(Resource.Success(email))
            } catch (e: Exception) {
                _resetPassword.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}

        }

        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}

