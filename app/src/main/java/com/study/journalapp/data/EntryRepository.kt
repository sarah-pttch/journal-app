package com.study.journalapp.data

import com.applandeo.materialcalendarview.CalendarDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class EntryRepository(
    private val dao: EntryDao
) {
    suspend fun createEntry(entry: Entry) {
        dao.insert(entry)
    }

    suspend fun update(entry: Entry) {
        dao.update(entry)
    }

    suspend fun delete(entry: Entry) {
        dao.delete(entry)
    }

    fun getAll(): Flow<List<Entry>> {
        return dao.getAll()
    }

    fun getById(entryId: Long): Flow<Entry?> {
        return dao.get(entryId)
    }

    fun getByMonth(year: Int, month: Int): Flow<List<Entry>> {
        return dao.getAll().map {entries ->
            entries.filter { entry ->
                val calendar = Calendar.getInstance()
                calendar.time = entry.creationDate

                val entryMonth = calendar.get(Calendar.MONTH) + 1
                val entryYear = calendar.get(Calendar.YEAR)

                entryMonth == month && entryYear == year
            }
        }
    }

    fun getByDay(day: CalendarDay): Flow<List<Entry>> {
        val start = day.calendar.clone() as Calendar
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        val end = start.clone() as Calendar
        end.add(Calendar.DAY_OF_MONTH, 1)

        return dao.getByDate(start.time, end.time)
    }
}