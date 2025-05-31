package com.drmiaji.hisnulmuslimtab

import android.app.Application
import com.drmiaji.hisnulmuslimtab.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslimtab.data.repository.HisnulMuslimRepository
import com.drmiaji.hisnulmuslimtab.utils.ThemeUtils

class MyApplication : Application() {

    lateinit var repository: HisnulMuslimRepository
        private set // Prevents external modification

    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applyTheme(this)

        val database = HisnulMuslimDatabase.getDatabase(this)
        repository = HisnulMuslimRepository(
            database.categoryDao(),
            database.duaNameDao(),
            database.duaDetailDao(),
            database.favoriteDao()
        )
    }
}