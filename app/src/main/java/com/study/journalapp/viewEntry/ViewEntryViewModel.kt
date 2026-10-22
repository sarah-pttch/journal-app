package com.study.journalapp.viewEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.launch

class ViewEntryViewModel(entryId: Long, val repository: EntryRepository) : ViewModel() {
    val entry = repository.getById(entryId).asLiveData()

    fun deleteEntry() {
        viewModelScope.launch {
            entry.value?.let { repository.delete(it) }
        }
    }
}