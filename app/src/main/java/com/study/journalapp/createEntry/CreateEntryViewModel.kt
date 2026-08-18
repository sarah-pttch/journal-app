package com.study.journalapp.createEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateEntryViewModel(val repository: EntryRepository) : ViewModel() {
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    fun onTitleChanged(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChanged(newContent: String) {
        _content.value = newContent
    }

    fun createEntry() {
        viewModelScope.launch {
            val entry = Entry(title = _title.value, content = _content.value)
            repository.createEntry(entry)
        }
    }
}