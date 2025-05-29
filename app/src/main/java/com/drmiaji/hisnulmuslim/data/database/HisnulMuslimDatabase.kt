package com.drmiaji.hisnulmuslim.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drmiaji.hisnulmuslim.data.dao.CategoryDao
import com.drmiaji.hisnulmuslim.data.dao.DuaDetailDao
import com.drmiaji.hisnulmuslim.data.dao.DuaNameDao
import com.drmiaji.hisnulmuslim.data.entities.Category
import com.drmiaji.hisnulmuslim.data.entities.DuaDetail
import com.drmiaji.hisnulmuslim.data.entities.DuaName

@Database(
    entities = [Category::class, DuaName::class, DuaDetail::class],
    version = 1,
    exportSchema = false
)
abstract class HisnulMuslimDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun duaNameDao(): DuaNameDao
    abstract fun duaDetailDao(): DuaDetailDao

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
                    .createFromAsset("databases/dua.db") // Replace with your actual SQLite file name
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