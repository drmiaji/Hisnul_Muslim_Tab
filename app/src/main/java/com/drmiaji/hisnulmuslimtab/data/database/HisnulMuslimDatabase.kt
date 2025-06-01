package com.drmiaji.hisnulmuslimtab.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
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
    version = 3,
    exportSchema = false
)
abstract class HisnulMuslimDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun duaNameDao(): DuaNameDao
    abstract fun duaDetailDao(): DuaDetailDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Make sure this matches exactly your FavoriteChapter entity
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `favorites` (
                `chapId` INTEGER NOT NULL PRIMARY KEY,
                `isFavorite` INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM category")
                // No schema changes, so we leave this empty
            }
        }

        @Volatile
        private var INSTANCE: HisnulMuslimDatabase? = null

        fun getDatabase(context: Context): HisnulMuslimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HisnulMuslimDatabase::class.java,
                    "hisnul_muslim_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .createFromAsset("databases/dua.db")
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