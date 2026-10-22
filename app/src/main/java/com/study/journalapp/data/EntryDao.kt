package com.study.journalapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EntryDao {

    @Insert
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM entries ORDER BY entryId DESC")
    fun getAll(): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE entryId = :entryId")
    fun get(entryId: Long): Flow<Entry?>

    @Query("SELECT * FROM entries WHERE creation_date BETWEEN :start AND :end")
    fun getByDate(start: Date, end: Date): Flow<List<Entry>>
}