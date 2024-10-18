package com.depi.myapplicatio.util.validation

data class RegisterFailedState(
    val email: RegisterValidation,
    val password: RegisterValidation
)
