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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MainTab {
    CATEGORY,
    CHAPTERS,
    FAVORITES
}

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

    // We'll keep a MutableStateFlow for favorites (list of chapter IDs)
    private val _favoriteChapterIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteChapterIds: StateFlow<Set<Int>> = _favoriteChapterIds

    // Expose favorite chapters as filtered from allChapters by favorite IDs
    val favoriteChapters: StateFlow<List<DuaName>> = combine(allChapters, favoriteChapterIds) { chapters, favIds ->
        chapters.filter { it.chap_id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredChapters: StateFlow<List<DuaName>> = combine(
        allChapters, selectedTab, selectedCategory, favoriteChapterIds
    ) { chapters, tab, cat, favIds ->

        when (tab) {
            MainTab.CHAPTERS -> chapters

            MainTab.CATEGORY -> cat?.let { c -> chapters.filter { it.category == c.name } } ?: emptyList()

            MainTab.FAVORITES -> chapters.filter { it.chap_id in favIds }
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

    /** Toggle favorite status for a chapter */
    fun toggleFavorite(chapter: DuaName) {
        viewModelScope.launch {
            if (isFavorite(chapter)) {
                repository.removeFavorite(chapter.chap_id)
            } else {
                repository.addFavorite(chapter.chap_id)
            }
        }
    }

    /** Check if a chapter is favorite */
    fun isFavorite(chapter: DuaName): Boolean {
        return chapter.chap_id in _favoriteChapterIds.value
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