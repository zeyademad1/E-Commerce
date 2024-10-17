package com.depi.myapplicatio.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import com.depi.myapplicatio.data.User
import com.depi.myapplicatio.util.RegisterFailedState
import com.depi.myapplicatio.util.RegisterValidation
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.util.validateEmail
import com.depi.myapplicatio.util.validatePassword
import com.depi.myapplicatio.viewmodel.Constants.USER_COLLECTION
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
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFailedState>()
    val validation = _validation.receiveAsFlow()
    fun createAccountWithEmailAndPassword(user: User, password: String) {

        if (CheckValidation(user, password)) {
            // أولاً، تحقق مما إذا كان البريد الإلكتروني موجودًا
            checkEmailExists(user.email) { emailExists ->
                if (emailExists) {
                    // إذا كان البريد الإلكتروني موجودًا، أرسل رسالة خطأ
                    runBlocking {
                        _validation.send(
                            RegisterFailedState(
                                email = RegisterValidation.Failed("This email is already in use."),
                                password = RegisterValidation.Success // أو أي قيمة مناسبة لحالة كلمة المرور
                            )
                        )
                    }
                } else {
                    // إذا لم يكن البريد الإلكتروني موجودًا، تابع إنشاء الحساب
                    runBlocking {
                        _register.emit(Resource.Loading())
                    }
                    firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                        .addOnSuccessListener {
                            it.user?.let {
                                SaveUserInfo(it.uid, user)
                                Log.d("Firestore", "User data added successfully.")
                            }
                        }.addOnFailureListener {
                            _register.value = Resource.Error(it.message.toString())
                            Log.w("Firestore", "Error adding user data", it)
                        }
                }
            }
        } else {
            val registerFailedState = RegisterFailedState(
                email = validateEmail(user.email),
                password = validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFailedState)
            }
        }
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        db.collection(USER_COLLECTION)
            .whereEqualTo("email", email) // استخدم الحقل المناسب من نموذج المستخدم
            .get()
            .addOnSuccessListener { documents ->
                // إذا كانت هناك أي وثائق، فهذا يعني أن البريد الإلكتروني موجود
                callback(!documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error checking email existence", exception)
                callback(false) // إذا حدث خطأ، اعتبر البريد الإلكتروني غير موجود
            }
    }

    private fun SaveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun CheckValidation(user: User, password: String): Boolean {
        val emailvalidation = validateEmail(user.email)
        val passwordvalidation = validatePassword(password)
        val shouldRegister = emailvalidation is RegisterValidation.Success
                && passwordvalidation is RegisterValidation.Success
        return shouldRegister
    }
}




