package com.study.journalapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.study.journalapp.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val languageCodes = listOf("en", "de")
    private val languageNames = listOf("English", "Deutsch")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.languageDropdown.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, languageNames)
        )

        val currentLanguage = AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: Locale.getDefault().language
        val index = languageCodes.indexOf(currentLanguage).coerceAtLeast(0)
        binding.languageDropdown.setText(languageNames[index], false)

        binding.languageDropdown.setOnItemClickListener { _, _, position, _ ->
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(languageCodes[position])
            )
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}