package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.databinding.FragmentGameResultBinding

class GameResultFragment : Fragment() {
    
    private var _binding: FragmentGameResultBinding? = null
    private val binding get() = _binding!!
    
    private val args: GameResultFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameResultBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        displayResults()
        setupClickListeners()
    }
    
    private fun displayResults() {
        val score = args.score
        val total = args.totalQuestions
        val gameMode = args.gameMode
        val percentage = if (total > 0) (score * 100 / total) else 0
        
        binding.gameModeTextView.text = gameMode
        binding.finalScoreTextView.text = getString(R.string.final_score, score, total)
        binding.percentageTextView.text = "$percentage%"
    }
    
    private fun setupClickListeners() {
        binding.playAgainButton.setOnClickListener {
            when (args.gameMode) {
                "Gender Guessing" -> findNavController().navigate(R.id.action_gameResultFragment_to_genderGameFragment)
                "Translation" -> findNavController().navigate(R.id.action_gameResultFragment_to_translationGameFragment)
                "Word Completion" -> findNavController().navigate(R.id.action_gameResultFragment_to_wordCompletionGameFragment)
            }
        }
        
        binding.mainMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameResultFragment_to_mainMenuFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
