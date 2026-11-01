package com.study.journalapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.study.journalapp.R
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = HomeViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hasEntryToday.collect { hasEntry ->
                        binding.entryReminder.setText(
                            if (hasEntry) R.string.today_entry_done else R.string.today_entry_pending
                        )
                    }
                }
                launch {
                    viewModel.lastEntry.collect { entry ->
                        entry?.let {
                            binding.lastEntryCard.entryTitle.text = it.title
                            binding.lastEntryCard.entryDate.text = it.creationDate.toString()
                            binding.lastEntryCard.entryContentPreview.text = it.content
                            binding.lastEntryContainer.isVisible = true
                        }
                    }
                }
            }
        }

        binding.lastEntryCard.root.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.lastEntry.first()?.let { entry ->
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToViewEntryFragment(entry.entryId)
                    )
                }
            }
        }

        return view
    }
}