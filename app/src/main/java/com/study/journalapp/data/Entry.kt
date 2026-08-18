package com.study.journalapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Date

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var entryId: Long = 0L,
    var title: String,
    var content: String,
    @ColumnInfo(name = "creation_date")
    var creationDate: Date = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
    }.time
)