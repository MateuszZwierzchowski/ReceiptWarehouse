package com.example.receiptwarehouse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.receiptwarehouse.databinding.FragmentMainPreviewBinding
import com.nikialeksey.hunspell.Hunspell

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

fun autocorrect(input: String): String {
    val words = input.split(" ")
    val correctedWords = mutableListOf<String>()
    val hunspell = Hunspell("pl_PL.aff", "pl_PL.dic")
    for (word in words) {
        val suggestions = hunspell.suggest(word)
        val correctedWord = if (suggestions.isNotEmpty()) {
            // Suggest the first suggestion
            suggestions[0]
        } else {
            // No suggestions, keep the original word
            word
        }
        correctedWords.add(correctedWord)
    }
    return correctedWords.joinToString(" ")
}




class MainPreviewFragment : Fragment() {

    private var _binding: FragmentMainPreviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainPreviewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_MainPreviewFragment_to_CameraFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}