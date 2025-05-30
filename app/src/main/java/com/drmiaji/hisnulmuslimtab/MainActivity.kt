package com.drmiaji.hisnulmuslimtab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import com.drmiaji.hisnulmuslimtab.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslimtab.data.repository.HisnulMuslimRepository
import com.drmiaji.hisnulmuslimtab.ui.MainScreen
import com.drmiaji.hisnulmuslimtab.ui.theme.MyAppTheme
import com.drmiaji.hisnulmuslimtab.utils.ThemeUtils
import com.drmiaji.hisnulmuslimtab.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val db = HisnulMuslimDatabase.getDatabase(applicationContext)
                val repository = HisnulMuslimRepository(
                    db.categoryDao(),
                    db.duaNameDao(),
                    db.duaDetailDao(),
                    db.favoriteDao()  // Add this line!
                )
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this) // Apply theme before super.onCreate
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode = ThemeUtils.getCurrentThemeMode(applicationContext)
            val useDarkTheme = when (themeMode) {
                ThemeUtils.THEME_LIGHT -> false
                ThemeUtils.THEME_DARK -> true
                else -> isSystemInDarkTheme()
            }
            MyAppTheme(useDarkTheme = useDarkTheme) {
                MainScreen(viewModel = mainViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyTheme(this)
    }
}
