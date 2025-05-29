package com.drmiaji.hisnulmuslim

import android.app.Application
import com.drmiaji.hisnulmuslim.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslim.data.repository.HisnulMuslimRepository
import com.drmiaji.hisnulmuslim.utils.ThemeUtils

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
            database.duaDetailDao()
        )
    }
}