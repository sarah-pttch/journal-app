package com.study.journalapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.study.journalapp.data.EntryRepository

class HomeViewModelFactory(private val repository: EntryRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}