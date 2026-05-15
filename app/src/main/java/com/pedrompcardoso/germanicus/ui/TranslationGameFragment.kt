package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.game.GameMode
import com.pedrompcardoso.germanicus.game.GameViewModel
import com.pedrompcardoso.germanicus.databinding.FragmentTranslationGameBinding

class TranslationGameFragment : Fragment() {
    
    private var _binding: FragmentTranslationGameBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: GameViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslationGameBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.startGame(GameMode.TRANSLATION)
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

        viewModel.totalQuestions.observe(viewLifecycleOwner) { total ->
            val score = viewModel.score.value ?: 0
            binding.scoreTextView.text = getString(R.string.score, score, total)
        }

        viewModel.remainingLives.observe(viewLifecycleOwner) { remainingLives ->
            binding.livesTextView.text = getString(R.string.lives, remainingLives, GameViewModel.MAX_TRANSLATION_LIVES)
        }

        viewModel.translationOptions.observe(viewLifecycleOwner) { options ->
            val optionButtons = listOf(binding.optionOneButton, binding.optionTwoButton, binding.optionThreeButton)
            optionButtons.forEachIndexed { index, button ->
                val option = options.getOrNull(index)
                button.text = option.orEmpty()
                button.isEnabled = option != null
                button.visibility = if (option != null) View.VISIBLE else View.GONE
            }
        }
        
        viewModel.showResult.observe(viewLifecycleOwner) { show ->
            binding.resultCard.visibility = if (show) View.VISIBLE else View.GONE
            binding.optionsLayout.visibility = if (show) View.GONE else View.VISIBLE
        }
        
        viewModel.isCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (isCorrect != null) {
                val word = viewModel.currentWord.value
                val correctAnswer = word?.english ?: ""
                
                binding.resultTextView.text = if (isCorrect) getString(R.string.correct) else getString(R.string.incorrect)
                binding.resultTextView.setTextColor(
                    if (isCorrect) requireContext().getColor(android.R.color.holo_green_dark)
                    else requireContext().getColor(android.R.color.holo_red_dark)
                )
                binding.correctAnswerTextView.text = getString(R.string.correct_answer, correctAnswer)
                binding.nextButton.text = if (viewModel.hasNoTranslationLives()) {
                    getString(R.string.view_results)
                } else {
                    getString(R.string.next)
                }
            }
        }
        
        viewModel.isGameActive.observe(viewLifecycleOwner) { isActive ->
            if (!isActive) {
                val score = viewModel.score.value ?: 0
                val total = viewModel.totalQuestions.value ?: 0
                val action = TranslationGameFragmentDirections.actionTranslationGameFragmentToGameResultFragment(
                    score = score,
                    totalQuestions = total,
                    gameMode = "Translation"
                )
                findNavController().navigate(action)
            }
        }
    }
    
    private fun setupClickListeners() {
        val optionButtons = listOf(binding.optionOneButton, binding.optionTwoButton, binding.optionThreeButton)
        optionButtons.forEach { button ->
            button.setOnClickListener {
                viewModel.checkTranslationAnswer(button.text.toString())
            }
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
