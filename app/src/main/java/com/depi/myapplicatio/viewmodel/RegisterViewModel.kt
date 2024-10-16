package com.depi.myapplicatio.viewmodel


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import com.depi.myapplicatio.data.User
import com.depi.myapplicatio.util.Constrants.USER_COLLETION
import com.depi.myapplicatio.util.RegisterFailedState
import com.depi.myapplicatio.util.RegisterValidation
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.util.validateEmail
import com.depi.myapplicatio.util.validatePassword
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking


import androidx.lifecycle.ViewModel
import com.depi.myapplicatio.util.*
import com.depi.myapplicatio.viewmodel.Constants.USER_COLLECTION
import com.depi.myapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,

    private val db:FirebaseFirestore

    private val db: FirebaseFirestore

) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register


     private val _validation= Channel<RegisterFailedState>()
      val validation=_validation.receiveAsFlow()
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if(CheckValidation(user, password)) {

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {

            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)


                .addOnSuccessListener {
                    it.user?.let {
                         SaveUserInfo(it.uid,user)

                        Log.d("Firestore", "User data added successfully.")

                    }

                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                    Log.w("Firestore", "Error adding user data", it)

                }
        }
        else{
            val registerfailedstate = RegisterFailedState(
                email = validateEmail(user.email),
                password = validatePassword(password)
            )
            runBlocking {
                _validation.send(registerfailedstate)
            }

        }
    }

    private fun SaveUserInfo(userUid:String,user:User) {
          db.collection(USER_COLLETION)
              .document(userUid)
              .set(user)
              .addOnSuccessListener {
                   _register.value = Resource.Success(user)
              }
              .addOnFailureListener {
                  _register.value = Resource.Error(it.message.toString())
              }
    }

    private fun CheckValidation(user: User, password: String):Boolean {
        val emailvalidation = validateEmail(user.email)
        val passwordvalidation = validatePassword(password)
        val shouldRegister = emailvalidation is RegisterValidation.Success
                && passwordvalidation is RegisterValidation.Success

                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success


        return shouldRegister
    }
}