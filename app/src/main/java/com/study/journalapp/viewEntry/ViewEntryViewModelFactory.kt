package com.study.journalapp.viewEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.journalapp.data.EntryRepository

class ViewEntryViewModelFactory(private val entryId: Long, private val repository: EntryRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewEntryViewModel::class.java)) {
            return ViewEntryViewModel(entryId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}