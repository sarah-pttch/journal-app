package com.study.journalapp.createEntry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentCreateEntryBinding

class CreateEntryFragment : Fragment() {
    private var _binding: FragmentCreateEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEntryBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = CreateEntryViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(CreateEntryViewModel::class.java)

        binding.createTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.onTitleChanged(text.toString())
        }
        binding.createContent.doOnTextChanged { text, _, _, _ ->
            viewModel.onContentChanged(text.toString())
        }

        binding.createButton.setOnClickListener {
            viewModel.createEntry()
            findNavController().popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}