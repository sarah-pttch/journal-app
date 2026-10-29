package com.study.journalapp

import com.applandeo.materialcalendarview.CalendarDay
import com.study.journalapp.calendar.CalendarViewModel
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private val repository = mockk<EntryRepository>()
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        every { repository.getByMonth(any(), any()) } returns flowOf(emptyList())
        every { repository.getByDay(any()) } returns flowOf(emptyList())
        viewModel = CalendarViewModel(repository)
    }

    @Test
    fun entries_initiallyQueriesCurrentMonth() = runTest {
        val now = Calendar.getInstance()
        val expectedYear = now.get(Calendar.YEAR)
        val expectedMonth = now.get(Calendar.MONTH) + 1

        viewModel.entries.first()

        verify(exactly = 1) { repository.getByMonth(expectedYear, expectedMonth) }
    }

    @Test
    fun entries_afterSetFilter_exposesEntriesOfGivenMonth() = runTest {
        val februaryEntries = listOf(Entry(entryId = 1L, title = "February entry", content = "February content"))
        every { repository.getByMonth(2026, 2) } returns flowOf(februaryEntries)

        viewModel.setFilter(2026, 2)

        assertEquals(februaryEntries, viewModel.entries.first())
    }

    @Test
    fun entries_filterChanges_switchesToNewMonth() = runTest {
        val januaryEntries = listOf(Entry(entryId = 1L, title = "January entry", content = "January content"))
        val februaryEntries = listOf(Entry(entryId = 1L, title = "February entry", content = "February content"))
        every { repository.getByMonth(2026, 1) } returns flowOf(januaryEntries)
        every { repository.getByMonth(2026, 2) } returns flowOf(februaryEntries)

        viewModel.setFilter(2026, 1)
        val results = mutableListOf<List<Entry>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.entries.toList(results)
        }
        viewModel.setFilter(2026, 2)

        assertEquals(listOf(januaryEntries, februaryEntries), results)
    }

    @Test
    fun dayEntries_initiallyQueriesToday() = runTest {
        val slot = slot<CalendarDay>()
        every { repository.getByDay(capture(slot)) } returns flowOf(emptyList())

        viewModel.dayEntries.first()

        val queried = slot.captured.calendar
        val today = Calendar.getInstance()
        assertEquals(today.get(Calendar.YEAR), queried.get(Calendar.YEAR))
        assertEquals(today.get(Calendar.MONTH), queried.get(Calendar.MONTH))
        assertEquals(today.get(Calendar.DAY_OF_MONTH), queried.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun dayEntries_afterSetSelectedDay_exposesEntriesOfGivenDay() = runTest {
        val day = CalendarDay(Calendar.getInstance().apply { set(2026, Calendar.FEBRUARY, 12) })
        val dayEntries = listOf(Entry(entryId = 1L, title = "Day entry", content = "Day content"))
        every { repository.getByDay(day) } returns flowOf(dayEntries)

        viewModel.setSelectedDay(day)

        assertEquals(dayEntries, viewModel.dayEntries.first())
    }

    @Test
    fun navigationEntryId_isInitiallyNull() {
        assertNull(viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryClicked_setsNavigationEntryId() {
        viewModel.onEntryClicked(12L)

        assertEquals(12L, viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryNavigated_clearsNavigationEntryId() {
        viewModel.onEntryClicked(12L)
        viewModel.onEntryNavigated()

        assertNull(viewModel.navigationEntryId.value)
    }

    @Test
    fun onEntryClicked_clickingSameEntryAfterNavigation_setsIdAgain() {
        viewModel.onEntryClicked(12L)
        viewModel.onEntryNavigated()
        viewModel.onEntryClicked(12L)

        assertEquals(12L, viewModel.navigationEntryId.value)
    }
}