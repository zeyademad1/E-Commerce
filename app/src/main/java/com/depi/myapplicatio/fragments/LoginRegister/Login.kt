package com.depi.myapplicatio.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.depi.myapplicatio.R
import com.depi.myapplicatio.activites.ShoppingActivity
import com.depi.myapplicatio.databinding.FragmentLoginBinding
import com.depi.myapplicatio.dialog.setupBottomSheetDialog
import com.depi.myapplicatio.util.Resource
import com.depi.myapplicatio.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Login : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHavaAccount.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_registerFragment)
        }

        binding.buttonLoginLogin.setOnClickListener {
            val email = binding.edEmailLogin.text.toString().trim()
            val password = binding.edPasswordLogin.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvForgetPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.resetPassword.collect { result ->
                        when (result) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                Snackbar.make(requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                            }
                            is Resource.Error -> {
                                Snackbar.make(requireView(), "Error: ${result.message}", Snackbar.LENGTH_LONG).show()
                            }
                            is Resource.Unspecified -> {
                                // Handle the unspecified case if needed
                            }
                        }
                    }
                }

                launch {
                    viewModel.login.collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                binding.buttonLoginLogin.startAnimation()
                            }
                            is Resource.Success -> {
                                binding.buttonLoginLogin.revertAnimation()
                                Intent(requireActivity(), ShoppingActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(this)
                                }
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                                binding.buttonLoginLogin.revertAnimation()
                            }
                            is Resource.Unspecified -> {
                                // Handle the unspecified case if needed
                            }
                        }
                    }
                }
            }
        }
    }
}
