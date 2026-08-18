package com.study.journalapp.list

import androidx.lifecycle.ViewModel
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListViewModel(val repository: EntryRepository) : ViewModel() {
    val entries = repository.getAll()

    private val _navigationEntryId = MutableStateFlow<Long?>(null)
    val navigationEntryId = _navigationEntryId.asStateFlow()

    fun onEntryClicked(entryId: Long) {
        _navigationEntryId.value = entryId
    }

    fun onEntryNavigated() {
        _navigationEntryId.value = null
    }
}