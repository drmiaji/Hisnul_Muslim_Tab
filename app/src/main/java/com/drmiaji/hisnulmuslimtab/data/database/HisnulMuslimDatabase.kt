package com.drmiaji.hisnulmuslimtab.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drmiaji.hisnulmuslimtab.data.dao.CategoryDao
import com.drmiaji.hisnulmuslimtab.data.dao.DuaDetailDao
import com.drmiaji.hisnulmuslimtab.data.dao.DuaNameDao
import com.drmiaji.hisnulmuslimtab.data.dao.FavoriteDao
import com.drmiaji.hisnulmuslimtab.data.entities.Category
import com.drmiaji.hisnulmuslimtab.data.entities.DuaDetail
import com.drmiaji.hisnulmuslimtab.data.entities.DuaName
import com.drmiaji.hisnulmuslimtab.data.entities.FavoriteChapter

@Database(
    entities = [Category::class, DuaName::class, DuaDetail::class, FavoriteChapter::class],
    version = 2, // Increment version if you change the schema
    exportSchema = false
)
abstract class HisnulMuslimDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun duaNameDao(): DuaNameDao
    abstract fun duaDetailDao(): DuaDetailDao
    abstract fun favoriteDao(): FavoriteDao  // <-- Add this method

    companion object {
        @Volatile
        private var INSTANCE: HisnulMuslimDatabase? = null

        fun getDatabase(context: Context): HisnulMuslimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisnulMuslimDatabase::class.java,
                    "hisnul_muslim_database"
                )
                    .createFromAsset("databases/dua.db")
                    .fallbackToDestructiveMigration(true) // true = drop all tables on version mismatch
                    .addCallback(DatabaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Optional: Database callback for initialization
        private object DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Optional: Add any initialization logic here
            }
        }
    }
}