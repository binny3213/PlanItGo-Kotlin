package com.example.planitgo_finalproject.ui.get_started

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.databinding.GetStartedLayoutBinding
import com.example.planitgo_finalproject.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetStartedFragment : Fragment() {
    private var _binding : GetStartedLayoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = GetStartedLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.startedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_getStartedFragment_to_destinationFragment)
        }
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDestroyView() {
        (activity as MainActivity).showBottomNavigation()
        super.onDestroyView()
        _binding = null
    }

}