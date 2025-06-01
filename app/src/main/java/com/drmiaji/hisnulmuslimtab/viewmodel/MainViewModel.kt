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
import kotlinx.coroutines.flow.asStateFlow
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

    // Tab and category selection
    private val _selectedTab = MutableStateFlow(MainTab.CATEGORY)
    val selectedTab: StateFlow<MainTab> = _selectedTab

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    // Favorite chapter IDs (used to filter favorites and show star icon state)
    private val _favoriteChapterIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteChapterIds: StateFlow<Set<Int>> = _favoriteChapterIds

    // Live category and chapter list
    val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allChapters: StateFlow<List<DuaName>> = repository.getAllDuaNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Filtered list of chapters based on selected tab, category, and favorites
    val filteredChapters: StateFlow<List<DuaName>> = combine(
        allChapters, selectedTab, selectedCategory, favoriteChapterIds
    ) { chapters, tab, cat, favIds ->
        when (tab) {
            MainTab.CHAPTERS -> chapters
            MainTab.CATEGORY -> cat?.let { c ->
                chapters.filter { it.category == c.name }
            } ?: emptyList()
            MainTab.FAVORITES -> chapters.filter { it.chap_id in favIds }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // Collect favorites on init and update internal state
    init {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favList ->
                _favoriteChapterIds.value = favList.map { it.chapId }.toSet()
            }
        }
    }

    // Select tab from UI
    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        if (tab == MainTab.CHAPTERS) {
            _selectedCategory.value = null // clear category if switching to CHAPTERS tab
        }
    }

    // Select a category (automatically switches to category tab)
    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        _selectedTab.value = MainTab.CATEGORY
    }

    // Check if a chapter is marked favorite
    fun isFavorite(chapter: DuaName): Boolean {
        return chapter.chap_id in _favoriteChapterIds.value
    }

    // Toggle favorite status
    fun toggleFavorite(chapter: DuaName) {
        // Optimistically update UI state immediately
        val current = _favoriteChapterIds.value.toMutableSet()
        if (chapter.chap_id in current) {
            current.remove(chapter.chap_id)
            _favoriteChapterIds.value = current
            viewModelScope.launch {
                repository.removeFavorite(chapter.chap_id)
            }
        } else {
            current.add(chapter.chap_id)
            _favoriteChapterIds.value = current
            viewModelScope.launch {
                repository.addFavorite(chapter.chap_id)
            }
        }
    }

    // For launching WebView: get first dua detail id of a chapter
    suspend fun getFirstDuaDetailIdForChapter(chapId: Int): Int? = withContext(Dispatchers.IO) {
        val duaDetails = repository.getDuaDetailsByGlobalId(chapId).firstOrNull()
        duaDetails?.firstOrNull()?.id
    }

    // ViewModel Factory for injection
    class Factory(private val repository: HisnulMuslimRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}