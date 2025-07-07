package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.data.Gender
import com.pedrompcardoso.germanicus.game.GameMode
import com.pedrompcardoso.germanicus.game.GameViewModel
import com.pedrompcardoso.germanicus.databinding.FragmentGenderGameBinding

class GenderGameFragment : Fragment() {
    
    private var _binding: FragmentGenderGameBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: GameViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenderGameBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.startGame(GameMode.GENDER_GUESSING)
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.germanWordTextView.text = word.german
        }
        
        viewModel.score.observe(viewLifecycleOwner) { score ->
            val total = viewModel.totalQuestions.value ?: 0
            binding.scoreTextView.text = getString(R.string.score, score, total)
        }
        
        viewModel.showResult.observe(viewLifecycleOwner) { show ->
            binding.resultCard.visibility = if (show) View.VISIBLE else View.GONE
            binding.genderButtonsLayout.visibility = if (show) View.GONE else View.VISIBLE
        }
        
        viewModel.isCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (isCorrect != null) {
                val word = viewModel.currentWord.value
                val correctAnswer = "${word?.gender?.article} ${word?.german}"
                
                binding.resultTextView.text = if (isCorrect) getString(R.string.correct) else getString(R.string.incorrect)
                binding.resultTextView.setTextColor(
                    if (isCorrect) requireContext().getColor(android.R.color.holo_green_dark)
                    else requireContext().getColor(android.R.color.holo_red_dark)
                )
                binding.correctAnswerTextView.text = getString(R.string.correct_answer, correctAnswer)
            }
        }
        
        viewModel.isGameActive.observe(viewLifecycleOwner) { isActive ->
            if (!isActive) {
                val score = viewModel.score.value ?: 0
                val total = viewModel.totalQuestions.value ?: 0
                val action = GenderGameFragmentDirections.actionGenderGameFragmentToGameResultFragment(
                    score = score,
                    totalQuestions = total,
                    gameMode = "Gender Guessing"
                )
                findNavController().navigate(action)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.derButton.setOnClickListener {
            viewModel.checkGenderAnswer(Gender.MASCULINE)
        }
        
        binding.dieButton.setOnClickListener {
            viewModel.checkGenderAnswer(Gender.FEMININE)
        }
        
        binding.dasButton.setOnClickListener {
            viewModel.checkGenderAnswer(Gender.NEUTER)
        }
        
        binding.nextButton.setOnClickListener {
            viewModel.nextQuestion()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 