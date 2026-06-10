package com.example.englishlearningapp

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [Index(value = ["englishWord"], unique = true)]
)
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val englishWord: String,
    val japaneseMeaning: String,
    val isVisible: Boolean = true
)
