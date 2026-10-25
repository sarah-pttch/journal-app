package com.study.journalapp

import com.study.journalapp.createEntry.CreateEntryViewModel
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class CreateEntryViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<EntryRepository>(relaxed = true)

    @Test
    fun saveEntry_inCreateMode_createsNewEntry() = runTest {
        val viewModel = CreateEntryViewModel(0L, repository)
        viewModel.onTitleChanged("Entry title")
        viewModel.onContentChanged("Entry content")

        viewModel.saveEntry()

        coVerify {
            repository.create(match { it.title == "Entry title" && it.content == "Entry content" })
        }
        coVerify(exactly = 0) { repository.update(any())}
    }

    @Test
    fun saveEntry_inEditMode_updatesExistingEntry() = runTest {
        val entry = Entry(entryId = 1L, title = "Old title", content = "Old content")
        every { repository.getById(1L) } returns flowOf(entry)
        val viewModel = CreateEntryViewModel(1L, repository)

        viewModel.onTitleChanged("New title")
        viewModel.saveEntry()

        coVerify { repository.update(entry.copy(title = "New title")) }
        coVerify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun saveEntry_inEditModeBeforeEntryLoaded_savesNothing() = runTest {
        every { repository.getById(1L) } returns MutableSharedFlow()
        val viewModel = CreateEntryViewModel(1L, repository)

        viewModel.saveEntry()

        coVerify(exactly = 0) { repository.create(any()) }
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun saveEntry_repositoryThrows_returnsFalse() = runTest {
        coEvery { repository.create(any()) } throws IllegalStateException("some error")
        val viewModel = CreateEntryViewModel(0L, repository)

        assertFalse(viewModel.saveEntry())
    }

    @Test
    fun editMode_prefillsTitleAndContent() {
        val entry = Entry(entryId = 1L, title = "Old title", content = "Old content")
        every { repository.getById(1L) } returns flowOf(entry)

        val viewModel = CreateEntryViewModel(1L, repository)

        assertEquals("Old title", viewModel.title.value)
        assertEquals("Old content", viewModel.content.value)
    }
}