package com.drmiaji.hisnulmuslimtab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.drmiaji.hisnulmuslimtab.data.database.HisnulMuslimDatabase
import com.drmiaji.hisnulmuslimtab.data.repository.HisnulMuslimRepository
import com.drmiaji.hisnulmuslimtab.ui.MainScreen
import com.drmiaji.hisnulmuslimtab.ui.theme.MyAppTheme
import com.drmiaji.hisnulmuslimtab.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val db = HisnulMuslimDatabase.getDatabase(applicationContext)
                val repository = HisnulMuslimRepository(
                    db.categoryDao(),
                    db.duaNameDao(),
                    db.duaDetailDao()
                )
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MainScreen(viewModel = mainViewModel)
            }
        }
    }
}
