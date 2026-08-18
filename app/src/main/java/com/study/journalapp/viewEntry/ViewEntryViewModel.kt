package com.study.journalapp.viewEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.study.journalapp.data.EntryRepository

class ViewEntryViewModel(entryId: Long, val repository: EntryRepository) : ViewModel() {
    val entry = repository.getById(entryId).asLiveData()
}