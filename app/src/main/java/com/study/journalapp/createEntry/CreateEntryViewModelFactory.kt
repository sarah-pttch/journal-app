package com.study.journalapp.createEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.journalapp.data.EntryRepository

class CreateEntryViewModelFactory(private val repository: EntryRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEntryViewModel::class.java)) {
            return CreateEntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}