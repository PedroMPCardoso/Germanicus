package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {
    
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.genderGameCard.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_genderGameFragment)
        }
        
        binding.translationGameCard.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_translationGameFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 