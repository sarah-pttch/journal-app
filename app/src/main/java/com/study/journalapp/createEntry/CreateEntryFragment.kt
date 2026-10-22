package com.study.journalapp.createEntry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentCreateEntryBinding
import kotlinx.coroutines.launch

class CreateEntryFragment : Fragment() {
    private var _binding: FragmentCreateEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateEntryBinding.inflate(inflater, container, false)
        val view = binding.root

        val entryId = CreateEntryFragmentArgs.fromBundle(requireArguments()).entryId

        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = CreateEntryViewModelFactory(entryId, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(CreateEntryViewModel::class.java)

        if (viewModel.isEditMode) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit entry"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.title.collect { title ->
                        if (binding.createTitle.text.toString() != title) {
                            binding.createTitle.setText(title)
                            binding.createTitle.setSelection(title.length)
                        }
                    }
                }
                launch {
                    viewModel.content.collect { content ->
                        if (binding.createContent.text.toString() != content) {
                            binding.createContent.setText(content)
                            binding.createContent.setSelection(content.length)
                        }
                    }
                }
            }
        }

        binding.createTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.onTitleChanged(text.toString())
        }
        binding.createContent.doOnTextChanged { text, _, _, _ ->
            viewModel.onContentChanged(text.toString())
        }

        binding.createButton.setOnClickListener {
            viewModel.saveEntry()
            findNavController().popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}