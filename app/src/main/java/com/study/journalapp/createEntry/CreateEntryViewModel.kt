package com.study.journalapp.createEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CreateEntryViewModel(entryId: Long, val repository: EntryRepository) : ViewModel() {
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _imagePaths = MutableStateFlow<List<String>>(emptyList())
    val imagePaths = _imagePaths.asStateFlow()

    val isEditMode = entryId != 0L

    private val _canSave = MutableStateFlow(!isEditMode)
    val canSave = _canSave.asStateFlow()

    private var existingEntry: Entry? = null

    init {
        if (isEditMode) {
            viewModelScope.launch {
                repository.getById(entryId).first()?.let { entry ->
                    existingEntry = entry
                    _title.value = entry.title
                    _content.value = entry.content
                    _imagePaths.value = entry.imagePaths
                    _canSave.value = true
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

    fun addImages(paths: List<String>) {
        _imagePaths.value += paths
    }

    fun removeImage(path: String) {
        _imagePaths.value -= path
    }

    suspend fun saveEntry(): Boolean {
        val existing = existingEntry
        return try {
            when {
                existing != null -> {
                    repository.update(
                        existing.copy(
                            title = _title.value,
                            content = _content.value,
                            imagePaths = _imagePaths.value
                        )
                    )
                    true
                }

                !isEditMode -> {
                    repository.create(
                        Entry(
                            title = _title.value,
                            content = _content.value,
                            imagePaths = _imagePaths.value
                        )
                    )
                    true
                }

                else -> false
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }
}