package com.drmiaji.hisnulmuslimtab.data.repository

import com.drmiaji.hisnulmuslimtab.data.dao.CategoryDao
import com.drmiaji.hisnulmuslimtab.data.dao.DuaDetailDao
import com.drmiaji.hisnulmuslimtab.data.dao.DuaNameDao
import com.drmiaji.hisnulmuslimtab.data.entities.Category
import com.drmiaji.hisnulmuslimtab.data.entities.DuaDetail
import com.drmiaji.hisnulmuslimtab.data.entities.DuaName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class HisnulMuslimRepository(
    private val categoryDao: CategoryDao,
    private val duaNameDao: DuaNameDao,
    private val duaDetailDao: DuaDetailDao
) {
    // Category operations
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    suspend fun getCategoryById(id: Int): Category? = categoryDao.getCategoryById(id)

    // DuaName operations - Use joined queries to get category names
    fun getAllDuaNames(): Flow<List<DuaName>> = duaNameDao.getAllDuaNamesWithCategoryNames()

    // Convert category name to ID, then get duas by category ID
    suspend fun getDuaNamesByCategory(categoryName: String): Flow<List<DuaName>> {
        // Find category ID by name
        val categories = categoryDao.getAllCategories().first()
        val categoryId = categories.find { it.name == categoryName }?.id?.toString()

        return if (categoryId != null) {
            duaNameDao.getDuaNamesByCategoryId(categoryId)
        } else {
            flowOf(emptyList())
        }
    }

    suspend fun getDuaNameByGlobalId(globalId: String): DuaName? =
        duaNameDao.getDuaNameByGlobalId(globalId)

    fun searchDuaNames(query: String): Flow<List<DuaName>> =
        duaNameDao.searchDuaNamesWithCategoryNames(query)

    fun getAllChapterNames(): Flow<List<String>> = duaNameDao.getAllChapterNames()

    // DuaDetail operations
    fun getDuaDetailsByGlobalId(globalId: Int): Flow<List<DuaDetail>> =
        duaDetailDao.getDuaDetailsByGlobalId(globalId)

    suspend fun getDuaDetailById(id: Int): DuaDetail? =
        duaDetailDao.getDuaDetailById(id)

    fun searchDuaDetails(query: String): Flow<List<DuaDetail>> =
        duaDetailDao.searchDuaDetails(query)

    // Combined operations
    suspend fun getDuaWithDetails(globalId: Int): Pair<DuaName?, List<DuaDetail>> {
        val details = duaDetailDao.getDuaDetailsByGlobalId(globalId).first()
        val duaName = duaNameDao.getDuaNameByGlobalId(globalId.toString())
        return Pair(duaName, details)
    }

    fun getAllDuaDetailsSorted(): Flow<List<DuaDetail>> =
        duaDetailDao.getAllDuaDetailsSorted()
}