package com.depi.myapplicatio.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.depi.myapplicatio.R
import com.depi.myapplicatio.data.User
import com.depi.myapplicatio.databinding.FragmentRegisterBinding
import com.depi.myapplicatio.util.RegisterValidation
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val TAG = "Norhan"

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating the layout for RegisterFragment")
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvHavaAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_login)
        }
        Log.d(TAG, "onViewCreated: View created, ready to set up listeners")

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                Log.d(TAG, "onClick: Register button clicked")
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()

                viewModel.createAccountWithEmailAndPassword(user, password)
                Log.d(TAG, "onClick: CreateAccountWithEmailAndPassword called")
            }
        }

        lifecycleScope.launch {
            Log.d(TAG, "lifecycleScope: Launching coroutine to observe register LiveData")
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d(TAG, "repeatOnLifecycle: Collecting register flow from ViewModel")
                viewModel.register.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Log.d(TAG, "collect: Register process is Loading")
                            binding.buttonRegisterRegister.startAnimation()
                        }
                        is Resource.Success -> {
                            Log.d(TAG, "collect: Register Success - ${result.data}")
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "collect: Register Error - ${result.message}")
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        else -> {
                            Log.d(TAG, "collect: Unknown result")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is RegisterValidation.Failed) {
                        binding.edEmailRegister.apply {
                            error = validation.email.message
                            requestFocus()
                        }
                    }
                    if (validation.password is RegisterValidation.Failed) {
                        binding.edPasswordRegister.apply {
                            error = validation.password.message
                            requestFocus()
                        }
                    }
                }
            }
        }
    }
}
