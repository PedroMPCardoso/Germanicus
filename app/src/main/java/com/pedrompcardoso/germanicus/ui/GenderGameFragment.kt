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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GenderGameFragment : Fragment() {
    
    private var _binding: FragmentGenderGameBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: GameViewModel by activityViewModels()
    private var seenHeartRewardEvents = 0
    private var heartRewardAnimation: Runnable? = null
    
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
                binding.nextButton.text = if (viewModel.hasNoLives()) {
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

        binding.helpButton.setOnClickListener {
            showGenderHelpDialog()
        }
    }

    private fun buildHeartsText(remainingLives: Int): String {
        val filledHearts = "♥".repeat(remainingLives.coerceIn(0, GameViewModel.MAX_LIVES))
        val emptyHearts = "♡".repeat((GameViewModel.MAX_LIVES - remainingLives).coerceAtLeast(0))
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

    private fun showGenderHelpDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.gender_help_title))
            .setMessage(getString(R.string.gender_help_tips))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        heartRewardAnimation?.let { binding.heartRewardCard.removeCallbacks(it) }
        _binding = null
    }
} 
