package com.study.journalapp.calendar

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
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.study.journalapp.EntryItemAdapter
import com.study.journalapp.databinding.FragmentCalendarBinding
import java.util.Calendar
import com.study.journalapp.R
import com.study.journalapp.data.EntryDatabase
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root
        val application = requireNotNull(this.activity).application
        val dao = EntryDatabase.getInstance(application).entryDao
        val repository = EntryRepository(dao)
        val viewModelFactory = CalendarViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(CalendarViewModel::class.java)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.entries.collect { entries ->
                    val calendarDays = entries
                        .map { it.creationDate }
                        .map { date ->
                            val calendar = Calendar.getInstance().apply {
                                time = date
                            }
                            CalendarDay(calendar).apply {
                                imageResource = R.drawable.ic_entry
                                labelColor = R.color.green
                            }
                        }
                    binding.calendar.setCalendarDays(calendarDays)
                }
            }
        }

        val listener = object : OnCalendarPageChangeListener {
            override fun onChange() {
                viewModel.setFilter(
                    binding.calendar.currentPageDate.get(Calendar.YEAR),
                    binding.calendar.currentPageDate.get(Calendar.MONTH) + 1
                )
            }
        }
        binding.calendar.setOnForwardPageChangeListener(listener)
        binding.calendar.setOnPreviousPageChangeListener(listener)
        binding.calendar.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                viewModel.setSelectedDay(calendarDay)
            }
        })

        val adapter = EntryItemAdapter { entryId ->
            viewModel.onEntryClicked(entryId)
        }
        binding.calendarEntryList.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayEntries.collect { entries ->
                    adapter.data = entries
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEntryId.collect { entryId ->
                    entryId?.let {
                        val action = CalendarFragmentDirections.actionCalendarFragmentToViewEntryFragment(entryId)
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