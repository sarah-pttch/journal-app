package com.study.journalapp.home

import androidx.lifecycle.ViewModel
import com.applandeo.materialcalendarview.CalendarDay
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class HomeViewModel(val repository: EntryRepository): ViewModel() {
    val hasEntryToday: Flow<Boolean> =
        repository.getByDay(CalendarDay(Calendar.getInstance()))
            .map { it.isNotEmpty() }

    val lastEntry: Flow<Entry?> = repository.getLast()
}