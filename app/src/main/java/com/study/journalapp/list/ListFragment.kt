package com.study.journalapp.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.study.journalapp.EntryItemAdapter
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.databinding.FragmentListBinding
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = ListViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ListViewModel::class.java)

        val adapter = EntryItemAdapter { entryId ->
            viewModel.onEntryClicked(entryId)
        }
        binding.entryList.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.entries.collect { entries ->
                    adapter.data = entries
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEntryId.collect { entryId ->
                    entryId?.let {
                        val action = ListFragmentDirections.actionListFragmentToViewEntryFragment(entryId)
                        findNavController().navigate(action)
                        viewModel.onEntryNavigated()
                    }
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}