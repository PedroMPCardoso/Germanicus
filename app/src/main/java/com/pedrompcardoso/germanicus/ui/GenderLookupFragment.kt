package com.pedrompcardoso.germanicus.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pedrompcardoso.germanicus.R
import com.pedrompcardoso.germanicus.data.GermanNouns
import com.pedrompcardoso.germanicus.databinding.FragmentGenderLookupBinding

class GenderLookupFragment : Fragment() {
    
    private var _binding: FragmentGenderLookupBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var germanNouns: GermanNouns
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenderLookupBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        germanNouns = GermanNouns(requireContext())
        setupClickListeners()
        setupTextWatcher()
    }
    
    private fun setupClickListeners() {
        binding.lookupButton.setOnClickListener {
            performLookup()
        }
        
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Allow lookup on "Done" key press
        binding.wordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLookup()
                true
            } else {
                false
            }
        }
    }
    
    private fun setupTextWatcher() {
        binding.wordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Hide result card when text changes
                binding.resultCard.visibility = View.GONE
            }
        })
    }
    
    private fun performLookup() {
        val inputWord = binding.wordEditText.text.toString().trim()
        
        if (inputWord.isEmpty()) {
            binding.wordInputLayout.error = "Please enter a word"
            return
        }
        
        binding.wordInputLayout.error = null
        
        // Capitalize first letter for lookup
        val lookupWord = inputWord.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
        
        val words = germanNouns[lookupWord]
        
        if (words.isEmpty()) {
            showWordNotFound()
        } else {
            showGenderResult(words[0], lookupWord)
        }
    }
    
    private fun showWordNotFound() {
        binding.resultCard.visibility = View.VISIBLE
        binding.resultTitleTextView.text = "Not Found"
        binding.wordTextView.text = binding.wordEditText.text.toString()
        binding.genderTextView.text = getString(R.string.word_not_found)
        binding.genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_color))
        binding.declensionTextView.visibility = View.GONE
    }
    
    private fun showGenderResult(noun: com.pedrompcardoso.germanicus.data.GermanNoun, word: String) {
        binding.resultCard.visibility = View.VISIBLE
        binding.resultTitleTextView.text = "Result"
        binding.wordTextView.text = word
        
        val genderText = when (noun.genus) {
            "m" -> getString(R.string.masculine)
            "f" -> getString(R.string.feminine)
            "n" -> getString(R.string.neuter)
            else -> "Unknown"
        }
        
        binding.genderTextView.text = genderText
        binding.genderTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_color))
        
        // Show declension forms
        val flexion = noun.flexion
        val declensionText = buildString {
            appendLine("Declension forms:")
            appendLine("Nominativ: ${flexion.nominativSingular} / ${flexion.nominativPlural}")
            appendLine("Genitiv: ${flexion.genitivSingular} / ${flexion.genitivPlural}")
            appendLine("Dativ: ${flexion.dativSingular} / ${flexion.dativPlural}")
            appendLine("Akkusativ: ${flexion.akkusativSingular} / ${flexion.akkusativPlural}")
            
            // Add alternative forms if they exist
            if (!flexion.nominativSingularAlt.isNullOrEmpty()) {
                appendLine("Nominativ alt: ${flexion.nominativSingularAlt}")
            }
            if (!flexion.genitivSingularAlt.isNullOrEmpty()) {
                appendLine("Genitiv alt: ${flexion.genitivSingularAlt}")
            }
            if (!flexion.dativSingularAlt.isNullOrEmpty()) {
                appendLine("Dativ alt: ${flexion.dativSingularAlt}")
            }
        }
        
        binding.declensionTextView.text = declensionText
        binding.declensionTextView.visibility = View.VISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
