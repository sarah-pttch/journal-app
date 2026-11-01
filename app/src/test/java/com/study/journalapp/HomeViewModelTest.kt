package com.study.journalapp

import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import com.study.journalapp.home.HomeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    private val repository = mockk<EntryRepository>(relaxed = true)

    @Test
    fun lastEntry_emitsLastEntry() = runTest {
        val entry = Entry(entryId = 1L, title = "Entry title", content = "Entry content")
        every { repository.getLast() } returns flowOf(entry)
        val viewModel = HomeViewModel(repository)
        assertEquals(entry, viewModel.lastEntry.first())
    }

    @Test
    fun lastEntry_noEntries_returnsNull() = runTest {
        every { repository.getLast() } returns flowOf(null)
        val viewModel = HomeViewModel(repository)
        assertNull(viewModel.lastEntry.first())
    }

    @Test
    fun hasEntryToday_noEntry_returnsFalse() = runTest {
        every { repository.getByDay(any()) } returns flowOf(emptyList())
        val viewModel = HomeViewModel(repository)
        assertFalse(viewModel.hasEntryToday.first())
    }

    @Test
    fun hasEntryToday_existingEntry_returnsTrue() = runTest {
        val entry = Entry(entryId = 1L, title = "Today's title", content = "Today's content")
        every { repository.getByDay(any()) } returns flowOf(listOf(entry))
        val viewModel = HomeViewModel(repository)
        assertTrue(viewModel.hasEntryToday.first())
    }
}