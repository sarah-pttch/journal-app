package com.study.journalapp.viewEntry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentViewEntryBinding

class ViewEntryFragment : Fragment() {
    private var _binding: FragmentViewEntryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewEntryBinding.inflate(inflater, container, false)
        val view = binding.root

        val entryId = ViewEntryFragmentArgs.fromBundle(requireArguments()).entryId

        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = ViewEntryViewModelFactory(entryId, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewEntryViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}