package com.depi.myapplicatio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.depi.myapplicatio.R
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LoginRegisterActivity", "onCreateView: Inflating the layout for LoginRegisterActivity ")

        setContentView(R.layout.activity_login_register)
    }
}