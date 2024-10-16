package com.depi.myapplicatio.util


import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Patterns

fun validateEmail(email: String): RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("email can not be Empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email Format")

    return RegisterValidation.Success

}

fun validatePassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("password can not be Empty")

    if(password.length<6)
        return RegisterValidation.Failed("password should contains 6 characters")
    return RegisterValidation.Success


import android.util.Patterns

fun validateEmail(email: String): RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")

    if (password.length < 6)
        return RegisterValidation.Failed("Password should contains 6 char")

    return RegisterValidation.Success

}