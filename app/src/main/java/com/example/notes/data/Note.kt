package com.example.notes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
) {
    fun copy(
        id: Int = this.id,
        title: String = this.title,
        content: String = this.content,
        timestamp: Long = this.timestamp
    ): Note {
        return Note(id, title, content, timestamp)
    }
}
