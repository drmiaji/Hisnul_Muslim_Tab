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
    version = 1,
    exportSchema = true
)
abstract class HisnulMuslimDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun duaNameDao(): DuaNameDao
    abstract fun duaDetailDao(): DuaDetailDao
    abstract fun favoriteDao(): FavoriteDao

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
                    .fallbackToDestructiveMigration(true)
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

//@Database(
//    entities = [Category::class, DuaName::class, DuaDetail::class, FavoriteChapter::class],
//    version = 5,
//    exportSchema = true
//)
//abstract class HisnulMuslimDatabase : RoomDatabase() {
//
//    abstract fun categoryDao(): CategoryDao
//    abstract fun duaNameDao(): DuaNameDao
//    abstract fun duaDetailDao(): DuaDetailDao
//    abstract fun favoriteDao(): FavoriteDao
//
//    companion object {
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Create favorites table while preserving any existing data
//                database.execSQL("""
//                    CREATE TABLE IF NOT EXISTS `favorites` (
//                        `chapId` INTEGER NOT NULL PRIMARY KEY,
//                        `isFavorite` INTEGER NOT NULL DEFAULT 1
//                    )
//                """.trimIndent())
//            }
//        }
//
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Backup favorites before clearing categories
//                database.execSQL("""
//                    CREATE TEMPORARY TABLE favorites_backup AS
//                    SELECT * FROM favorites
//                """)
//
//                // Clear category data
//                database.execSQL("DELETE FROM category")
//
//                // Restore favorites (they should remain intact)
//                // This step is optional since we're not touching favorites table
//            }
//        }
//
//        // Fixed: Use underscore instead of hyphen
//        val MIGRATION_3_4 = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Preserve favorites during category data refresh
//                database.execSQL("""
//                    CREATE TEMPORARY TABLE temp_favorites AS
//                    SELECT chapId, isFavorite FROM favorites
//                """)
//
//                // Clear category data for fresh import
//                database.execSQL("DELETE FROM category")
//
//                // Favorites table structure remains the same, no changes needed
//                // The temp table will be automatically dropped after migration
//            }
//        }
//
//        @Volatile
//        private var INSTANCE: HisnulMuslimDatabase? = null
//
//        fun getDatabase(context: Context): HisnulMuslimDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    HisnulMuslimDatabase::class.java,
//                    "hisnul_muslim_database"
//                )
//                    // Fixed: Use underscore in migration name
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
//                    .createFromAsset("databases/dua.db")
//                    .addCallback(DatabaseCallback)
//                    // Add fallback strategy to preserve user data
//                    .fallbackToDestructiveMigrationOnDowngrade()
//                    .build()
//                INSTANCE = instance
//                instance
//            }
//        }
//
//        // Enhanced callback to handle data preservation
//        private object DatabaseCallback : Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                // Database created from asset, favorites table will be empty initially
//            }
//
//            override fun onOpen(db: SupportSQLiteDatabase) {
//                super.onOpen(db)
//                // Ensure favorites table exists (safety check)
//                db.execSQL("""
//                    CREATE TABLE IF NOT EXISTS `favorites` (
//                        `chapId` INTEGER NOT NULL PRIMARY KEY,
//                        `isFavorite` INTEGER NOT NULL DEFAULT 1
//                    )
//                """)
//            }
//        }
//    }
//}