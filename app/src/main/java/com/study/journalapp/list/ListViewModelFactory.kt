package com.study.journalapp.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.journalapp.data.EntryRepository

class ListViewModelFactory(private val repository: EntryRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}