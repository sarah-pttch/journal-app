package com.study.journalapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.viewEntry.ViewEntryViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ViewEntryViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository = mockk<EntryRepository>(relaxed = true)

    @Test
    fun entry_emitsLoadedEntry() {
        val entry = Entry(entryId = 1L, title = "Entry title", content = "Entry content")
        every { repository.getById(1L) } returns flowOf(entry)
        val viewModel = ViewEntryViewModel(1L, repository)

        val observer = Observer<Entry?> { }
        viewModel.entry.observeForever(observer)

        assertEquals(entry, viewModel.entry.value)

        viewModel.entry.removeObserver(observer)
    }

    @Test
    fun deleteEntry_deletesLoadedEntry() {
        val entry = Entry(entryId = 1L, title = "Entry title", content = "Entry content")
        every { repository.getById(1L) } returns flowOf(entry)
        val viewModel = ViewEntryViewModel(1L, repository)

        val observer = Observer<Entry?> { }
        viewModel.entry.observeForever(observer)

        viewModel.deleteEntry()

        coVerify(exactly = 1) { repository.delete(entry) }

        viewModel.entry.removeObserver(observer)
    }

    @Test
    fun deleteEntry_noEntryLoaded_deletesNothing() {
        every { repository.getById(1L) } returns flowOf<Entry?>(null)
        val viewModel = ViewEntryViewModel(1L, repository)

        val observer = Observer<Entry?> { }
        viewModel.entry.observeForever(observer)

        viewModel.deleteEntry()

        coVerify(exactly = 0) { repository.delete(any()) }

        viewModel.entry.removeObserver(observer)
    }
}