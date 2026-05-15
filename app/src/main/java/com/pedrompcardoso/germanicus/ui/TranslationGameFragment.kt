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
    private var seenHeartRewardEvents = 0
    private var heartRewardAnimation: Runnable? = null
    
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
        seenHeartRewardEvents = viewModel.heartRewardEvents.value ?: 0

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
            binding.livesTextView.text = buildHeartsText(remainingLives)
        }

        viewModel.heartRewardEvents.observe(viewLifecycleOwner) { rewardEvents ->
            if (rewardEvents > seenHeartRewardEvents) {
                seenHeartRewardEvents = rewardEvents
                showHeartRewardAnimation()
            }
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

    private fun buildHeartsText(remainingLives: Int): String {
        val filledHearts = "♥".repeat(remainingLives.coerceIn(0, GameViewModel.MAX_TRANSLATION_LIVES))
        val emptyHearts = "♡".repeat((GameViewModel.MAX_TRANSLATION_LIVES - remainingLives).coerceAtLeast(0))
        return filledHearts + emptyHearts
    }

    private fun showHeartRewardAnimation() {
        heartRewardAnimation?.let { binding.heartRewardCard.removeCallbacks(it) }

        binding.heartRewardCard.apply {
            alpha = 0f
            translationY = -24f
            scaleX = 0.92f
            scaleY = 0.92f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(220L)
                .start()
        }

        heartRewardAnimation = Runnable {
            binding.heartRewardCard.animate()
                .alpha(0f)
                .translationY(-24f)
                .setDuration(260L)
                .withEndAction {
                    binding.heartRewardCard.visibility = View.GONE
                }
                .start()
        }

        binding.heartRewardCard.postDelayed(heartRewardAnimation, 1400L)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        heartRewardAnimation?.let { binding.heartRewardCard.removeCallbacks(it) }
        _binding = null
    }
}
