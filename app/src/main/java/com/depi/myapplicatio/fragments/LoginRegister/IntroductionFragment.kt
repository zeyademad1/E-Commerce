package com.depi.myapplicatio.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.depi.myapplicatio.R
import com.depi.myapplicatio.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private var _binding: FragmentIntroductionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.FirstClicker.setOnClickListener {

            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
