package com.example.englishlearningapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY englishWord ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isVisible = 1 ORDER BY englishWord ASC")
    fun getVisibleWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE englishWord LIKE :query || '%' OR japaneseMeaning LIKE :query || '%'")
    fun searchWords(query: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE englishWord = :englishWord LIMIT 1")
    suspend fun getWordByEnglish(englishWord: String): Word?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(word: Word)

    @Update
    suspend fun update(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Query("UPDATE words SET isVisible = 1")
    suspend fun showAllWords()
}
