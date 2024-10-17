package com.depi.myapplicatio.util

data class RegisterFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
) {

}
