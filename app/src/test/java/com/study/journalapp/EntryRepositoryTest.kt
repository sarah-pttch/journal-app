package com.study.journalapp

import com.applandeo.materialcalendarview.CalendarDay
import com.study.journalapp.data.Entry
import com.study.journalapp.data.EntryDao
import com.study.journalapp.data.EntryRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.Date

class EntryRepositoryTest {
    private val dao = mockk<EntryDao>(relaxed = true)
    private val repository = EntryRepository(dao)

    private fun entryOn(
        id: Long, year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0
    ): Entry {
        val calendar = Calendar.getInstance().apply {
            clear()
            set(year, month - 1, day, hour, minute, 0)
        }
        return Entry(entryId = id, title = "Entry $id", content = "Content", creationDate = calendar.time)
    }

    private fun dateOf(year: Int, month: Int, day: Int): Date =
        Calendar.getInstance().apply { clear(); set(year, month - 1, day) }.time

    @Test
    fun create_insertsEntryIntoDao() = runTest {
        val entry = entryOn(1L, 2026, 6, 26)
        repository.create(entry)
        coVerify(exactly = 1) { dao.insert(entry) }
    }

    @Test
    fun update_updatesEntryInDao() = runTest {
        val entry = entryOn(1L, 2026, 6, 26)
        repository.update(entry)
        coVerify(exactly = 1) { dao.update(entry) }
    }

    @Test
    fun delete_deletesEntryFromDao() = runTest {
        val entry = entryOn(1L, 2026, 6, 26)
        repository.delete(entry)
        coVerify(exactly = 1) { dao.delete(entry) }
    }

    @Test
    fun getAll_returnsEntriesFromDao() = runTest {
        val entries = listOf(entryOn(1L, 2026, 6, 26), entryOn(2L, 2026, 7, 26))
        every { dao.getAll() } returns flowOf(entries)
        assertEquals(entries, repository.getAll().first())
    }

    @Test
    fun getById_returnsEntryFromDao() = runTest {
        val entry = entryOn(1L, 2026, 6, 26)
        every { dao.get(1L) } returns flowOf(entry)
        assertEquals(entry, repository.getById(1L).first())
    }

    @Test
    fun getById_NonExistingEntry_returnsNull() = runTest {
        every { dao.get(12L) } returns flowOf(null)
        assertNull(repository.getById(12L).first())
    }

    @Test
    fun getByMonth_returnsOnlyEntriesOfRequestedMonthAndYear() = runTest {
        val june2026 = entryOn(1L, 2026, 6, 26)
        val february2026 = entryOn(2L, 2026, 2, 26)
        val june2025 = entryOn(3L, 2025, 6, 26)
        every { dao.getAll() } returns flowOf(listOf(june2026, february2026, june2025))

        assertEquals(listOf(june2026), repository.getByMonth(2026, 6).first())
    }

    @Test
    fun getByMonth_noMatchingEntries_returnsEmptyList() = runTest {
        every { dao.getAll() } returns flowOf(listOf(entryOn(1L, 2026, 6, 26)))
        assertEquals(emptyList<Entry>(), repository.getByMonth(2026, 2).first())
    }

    @Test
    fun getByMonth_includesFirstAndLastMomentOfMonthButNotNeighbors() = runTest {
        val first = entryOn(1L, 2026, 6, 1, hour = 0, minute = 0)
        val last = entryOn(2L, 2026, 6, 30, hour = 23, minute = 59)
        val dayBefore = entryOn(3L, 2026, 5, 31, hour = 23, minute = 59)
        val dayAfter = entryOn(4L, 2026, 7, 1, hour = 0, minute = 0)
        every { dao.getAll() } returns flowOf(listOf(dayBefore, first, last, dayAfter))

        assertEquals(listOf(first, last), repository.getByMonth(2026, 6).first())
    }

    @Test
    fun getByDay_returnsEntriesFromDaoForGivenDay() = runTest {
        val dayEntries = listOf(entryOn(1L, 2026, 6, 26))
        every { dao.getByDate(any(), any()) } returns flowOf(emptyList())
        every { dao.getByDate(dateOf(2026, 6, 26), dateOf(2026, 6, 27)) } returns flowOf(dayEntries)

        val day = CalendarDay(Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JUNE, 26, 12, 12)
        })

        assertEquals(dayEntries, repository.getByDay(day).first())
    }

    @Test
    fun getByDay_queriesDaoFromMidnightToNextMidnight() {
        val input = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JUNE, 26, 12, 12, 12)
            set(Calendar.MILLISECOND, 345)
        }
        val start = slot<Date>()
        val end = slot<Date>()
        every { dao.getByDate(capture(start), capture(end)) } returns flowOf(emptyList())

        repository.getByDay(CalendarDay(input))

        assertEquals(dateOf(2026, 6, 26), start.captured)
        assertEquals(dateOf(2026, 6, 27), end.captured)
    }
}