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
    private val _selectedTab = MutableStateFlow(MainTab.CHAPTERS)
    val selectedTab: StateFlow<MainTab> = _selectedTab

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    // Favorite chapter IDs (used to filter favorites and show star icon state)
    private val _favoriteChapterIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteChapterIds: StateFlow<Set<Int>> = _favoriteChapterIds

    private val _pendingAdds = MutableStateFlow<Set<Int>>(emptySet())
    val pendingAdds: StateFlow<Set<Int>> = _pendingAdds

    private val _pendingRemoves = MutableStateFlow<Set<Int>>(emptySet())
    val pendingRemoves: StateFlow<Set<Int>> = _pendingRemoves

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
                val dbSet = favList.map { it.chapId }.toSet()
                _favoriteChapterIds.value = dbSet
                // Remove IDs from pending sets if DB matches the change
                _pendingAdds.value = _pendingAdds.value.filter { it !in dbSet }.toSet()
                _pendingRemoves.value = _pendingRemoves.value.filter { it in dbSet }.toSet()
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
        val chapId = chapter.chap_id
        val isFav = favoriteChapterIds.value.contains(chapId)
        if (isFav) {
            _pendingRemoves.value = _pendingRemoves.value + chapId
            viewModelScope.launch {
                repository.removeFavorite(chapId)
            }
        } else {
            _pendingAdds.value = _pendingAdds.value + chapId
            viewModelScope.launch {
                repository.addFavorite(chapId)
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

    suspend fun getChaptersMatchingQuery(query: String): List<DuaName> {
        if (query.isBlank()) return allChapters.value
        // 1. Match chapters by name
        val nameMatches = allChapters.value.filter { it.chapname?.contains(query, ignoreCase = true) == true }
        // 2. Match chapters by dua detail content
        val detailIds = repository.getChapterIdsMatchingDuaDetails(query)
        val detailMatches = allChapters.value.filter { it.chap_id in detailIds }
        // 3. Merge and deduplicate
        return (nameMatches + detailMatches).distinctBy { it.chap_id }
    }
}