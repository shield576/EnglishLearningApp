package com.example.englishlearningapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS words_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                englishWord TEXT NOT NULL,
                japaneseMeaning TEXT NOT NULL,
                isVisible INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())
        // 重複がある場合は先勝ちで1件だけ残す
        database.execSQL("""
            INSERT OR IGNORE INTO words_new
            SELECT id, englishWord, japaneseMeaning, isVisible FROM words
        """.trimIndent())
        database.execSQL("DROP TABLE words")
        database.execSQL("ALTER TABLE words_new RENAME TO words")
        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_words_englishWord ON words (englishWord)"
        )
    }
}

@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
