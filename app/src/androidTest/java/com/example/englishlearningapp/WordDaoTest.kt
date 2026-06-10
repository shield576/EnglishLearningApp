package com.example.englishlearningapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var wordDao: WordDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        wordDao = db.wordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetWord() = runBlocking {
        val word = Word(englishWord = "apple", japaneseMeaning = "りんご")
        wordDao.insert(word)
        val allWords = wordDao.getAllWords().first()
        assertEquals(allWords[0], word.copy(id = 1))
    }

    @Test
    @Throws(Exception::class)
    fun getAllWords() = runBlocking {
        val word1 = Word(englishWord = "apple", japaneseMeaning = "りんご")
        val word2 = Word(englishWord = "banana", japaneseMeaning = "バナナ")
        wordDao.insert(word1)
        wordDao.insert(word2)
        val allWords = wordDao.getAllWords().first()
        assertEquals(allWords.size, 2)
    }
    
    @Test
    @Throws(Exception::class)
    fun updateWord() = runBlocking {
        val originalWord = Word(englishWord = "apple", japaneseMeaning = "りんご", isVisible = true)
        wordDao.insert(originalWord)
        
        // データベースから単語を取得し、IDを確認
        val insertedWord = wordDao.getAllWords().first().first()
        
        // isVisibleをfalseに更新
        val updatedWord = insertedWord.copy(isVisible = false)
        wordDao.update(updatedWord)
        
        val allWords = wordDao.getAllWords().first()
        assertEquals(allWords.size, 1)
        assertEquals(allWords[0].isVisible, false)
        assertEquals(allWords[0].englishWord, "apple")
    }

    @Test
    @Throws(Exception::class)
    fun deleteAll() = runBlocking {
        val word = Word(englishWord = "apple", japaneseMeaning = "りんご")
        wordDao.insert(word)
        wordDao.deleteAll()
        val allWords = wordDao.getAllWords().first()
        assertTrue(allWords.isEmpty())
    }
}
