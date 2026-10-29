package com.study.journalapp.calendar

import androidx.lifecycle.ViewModel
import com.applandeo.materialcalendarview.CalendarDay
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(val repository: EntryRepository) : ViewModel() {

    val calendar = Calendar.getInstance()

    private val filter = MutableStateFlow(CalendarFilter(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1
    ))

    private val selectedDay = MutableStateFlow(CalendarDay(Calendar.getInstance()))

    val entries = filter.flatMapLatest { f ->
        repository.getByMonth(f.year, f.month)
    }

    val dayEntries = selectedDay
        .flatMapLatest { day ->
            repository.getByDay(day)
        }

    private val _navigationEntryId = MutableStateFlow<Long?>(null)
    val navigationEntryId = _navigationEntryId.asStateFlow()

    fun setFilter(year: Int, month: Int) {
        filter.value = CalendarFilter(year, month)
    }

    fun setSelectedDay(day: CalendarDay) {
        selectedDay.value = day
    }

    fun onEntryClicked(entryId: Long) {
        _navigationEntryId.value = entryId
    }

    fun onEntryNavigated() {
        _navigationEntryId.value = null
    }
}