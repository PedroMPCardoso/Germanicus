package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.databinding.FragmentWordCompletionGameBinding
import com.pedrompcardoso.germanicus.game.GameMode
import com.pedrompcardoso.germanicus.game.GameViewModel

class WordCompletionGameFragment : Fragment() {

    private var _binding: FragmentWordCompletionGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()
    private var seenHeartRewardEvents = 0
    private var heartRewardAnimation: Runnable? = null
    private val letterButtons = mutableListOf<Button>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordCompletionGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startGame(GameMode.WORD_COMPLETION)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        seenHeartRewardEvents = viewModel.heartRewardEvents.value ?: 0

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.englishWordTextView.text = word.english
            updateCompletionAnswer()
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

        viewModel.completionLetterOptions.observe(viewLifecycleOwner) { options ->
            renderLetterButtons(options)
        }

        viewModel.completionSelectedIndices.observe(viewLifecycleOwner) { selectedIndices ->
            updateLetterButtons(selectedIndices)
        }

        viewModel.completionAnswerSlots.observe(viewLifecycleOwner) {
            updateCompletionAnswer()
        }

        viewModel.completionRevealedIndices.observe(viewLifecycleOwner) {
            updateCompletionAnswer()
        }

        viewModel.showResult.observe(viewLifecycleOwner) { show ->
            binding.resultCard.visibility = if (show) View.VISIBLE else View.GONE
            binding.letterGridLayout.visibility = if (show) View.GONE else View.VISIBLE
            binding.editButtonsLayout.visibility = if (show) View.GONE else View.VISIBLE
            binding.tipButton.isEnabled = !show
        }

        viewModel.isCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (isCorrect != null) {
                val correctAnswer = viewModel.currentWord.value?.german ?: ""

                binding.resultTextView.text = if (isCorrect) getString(R.string.correct) else getString(R.string.incorrect)
                binding.resultTextView.setTextColor(
                    if (isCorrect) ContextCompat.getColor(requireContext(), R.color.success_color)
                    else ContextCompat.getColor(requireContext(), R.color.error_color)
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
                val action = WordCompletionGameFragmentDirections.actionWordCompletionGameFragmentToGameResultFragment(
                    score = score,
                    totalQuestions = total,
                    gameMode = getString(R.string.word_completion)
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun setupClickListeners() {
        binding.backspaceButton.setOnClickListener {
            viewModel.removeLastCompletionLetter()
        }

        binding.clearButton.setOnClickListener {
            viewModel.clearCompletionAnswer()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitWordCompletionAnswer()
        }

        binding.tipButton.setOnClickListener {
            viewModel.revealCompletionHint()
        }

        binding.nextButton.setOnClickListener {
            viewModel.nextQuestion()
        }
    }

    private fun renderLetterButtons(options: List<String>) {
        binding.letterGridLayout.removeAllViews()
        letterButtons.clear()

        options.forEachIndexed { index, letter ->
            val button = Button(requireContext()).apply {
                text = letter
                textSize = 18f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
                setOnClickListener {
                    viewModel.selectCompletionLetter(index)
                }
            }

            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply {
                width = 0
                height = resources.getDimensionPixelSize(R.dimen.word_completion_letter_button_height)
                setMargins(6, 6, 6, 6)
            }

            binding.letterGridLayout.addView(button, params)
            letterButtons.add(button)
        }
    }

    private fun updateLetterButtons(selectedIndices: List<Int>) {
        letterButtons.forEachIndexed { index, button ->
            val selected = index in selectedIndices
            button.isEnabled = !selected
            button.alpha = if (selected) 0.35f else 1f
        }
    }

    private fun updateCompletionAnswer() {
        val word = viewModel.currentWord.value?.german.orEmpty()
        val answerSlots = viewModel.completionAnswerSlots.value.orEmpty()
        val revealedIndices = viewModel.completionRevealedIndices.value.orEmpty()

        binding.completionAnswerTextView.text = buildString {
            word.forEachIndexed { index, letter ->
                if (index > 0) append(" ")
                if (index in revealedIndices) {
                    append(letter.uppercaseChar())
                } else {
                    append(answerSlots.getOrNull(index) ?: '_')
                }
            }
        }
        binding.submitButton.isEnabled = word.isNotEmpty() && word.indices.all { index ->
            index in revealedIndices || answerSlots.getOrNull(index) != null
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

    override fun onDestroyView() {
        super.onDestroyView()
        heartRewardAnimation?.let { binding.heartRewardCard.removeCallbacks(it) }
        _binding = null
    }
}
