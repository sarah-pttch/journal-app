package com.study.journalapp

import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.list.ListViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ListViewModelTest {
    private val repository = mockk<EntryRepository>()

    private fun createViewModel(entries: List<Entry> = emptyList()): ListViewModel {
        every { repository.getAll() } returns flowOf(entries)
        return ListViewModel(repository)
    }

    @Test
    fun entries_exposesEntriesFromRepository() = runTest {
        val entries = listOf(
            Entry(entryId = 1L, title = "First entry", content = "First content"),
            Entry(entryId = 2L, title = "Second entry", content = "Second content")
        )

        val viewModel = createViewModel(entries)

        assertEquals(entries, viewModel.entries.first())
        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun navigationEntryId_isInitiallyNull() {
        val viewModel = createViewModel()

        assertNull(viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryClicked_setsNavigationEntryId() {
        val viewModel = createViewModel()

        viewModel.onEntryClicked(12L)

        assertEquals(12L, viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryNavigated_clearsNavigationEntryId() {
        val viewModel = createViewModel()

        viewModel.onEntryClicked(12L)
        viewModel.onEntryNavigated()

        assertNull(viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryClicked_clickingSameEntryAfterNavigation_setsIdAgain() {
        val viewModel = createViewModel()

        viewModel.onEntryClicked(12L)
        viewModel.onEntryNavigated()
        viewModel.onEntryClicked(12L)

        assertEquals(12L, viewModel.navigationEntryId.value)
    }
}