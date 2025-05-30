package com.drmiaji.hisnulmuslimtab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.drmiaji.hisnulmuslimtab.data.entities.Category
import com.drmiaji.hisnulmuslimtab.data.entities.DuaName
import com.drmiaji.hisnulmuslimtab.data.repository.HisnulMuslimRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

enum class MainTab { CATEGORY, CHAPTERS }

class MainViewModel(
    private val repository: HisnulMuslimRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(MainTab.CATEGORY)
    val selectedTab: StateFlow<MainTab> = _selectedTab

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allChapters: StateFlow<List<DuaName>> = repository.getAllDuaNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredChapters: StateFlow<List<DuaName>> = combine(
        allChapters, selectedTab, selectedCategory
    ) { chapters, tab, cat ->
        when (tab) {
            MainTab.CHAPTERS -> chapters
            MainTab.CATEGORY -> cat?.let { chapters.filter { it.category == cat.name } } ?: emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        if (tab == MainTab.CHAPTERS) _selectedCategory.value = null
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        _selectedTab.value = MainTab.CATEGORY
    }

    class Factory(private val repository: HisnulMuslimRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
    suspend fun getFirstDuaDetailIdForChapter(chapId: Int): Int? = withContext(Dispatchers.IO) {
        val duaDetails = repository.getDuaDetailsByGlobalId(chapId).firstOrNull()
        duaDetails?.firstOrNull()?.id
    }
}