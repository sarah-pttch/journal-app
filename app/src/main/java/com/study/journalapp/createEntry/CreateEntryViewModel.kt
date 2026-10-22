package com.study.journalapp.createEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CreateEntryViewModel(entryId: Long, val repository: EntryRepository) : ViewModel() {
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    val isEditMode = entryId != 0L
    private var existingEntry: Entry? = null

    init {
        if (isEditMode) {
            viewModelScope.launch {
                repository.getById(entryId).first()?.let { entry ->
                    existingEntry = entry
                    _title.value = entry.title
                    _content.value = entry.content
                }
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChanged(newContent: String) {
        _content.value = newContent
    }

    fun saveEntry() {
        viewModelScope.launch {
            val existing = existingEntry
            when {
                existing != null -> repository.update(
                    existing.copy(title = _title.value, content = _content.value)
                )
                !isEditMode -> repository.createEntry(
                    Entry(title = _title.value, content = _content.value)
                )
            }
        }
    }
}