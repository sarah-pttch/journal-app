package com.study.journalapp.viewEntry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.study.journalapp.R
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

        viewModel.entry.observe(viewLifecycleOwner) { entry ->
            entry?.let {
                (requireActivity() as AppCompatActivity).supportActionBar?.title = it.title
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_view_entry, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        findNavController().navigate(
                            ViewEntryFragmentDirections.actionViewEntryFragmentToCreateEntryFragment(entryId)
                        )
                        true
                    }
                    R.id.action_delete -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete entry?")
                            .setMessage("Do you really want to permanently delete this entry?")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete") { _, _ ->
                                viewModel.deleteEntry()
                                findNavController().navigateUp()
                            }
                            .show()
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}