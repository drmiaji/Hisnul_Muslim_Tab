package com.drmiaji.hisnulmuslim.data.dao

import androidx.room.*
import com.drmiaji.hisnulmuslim.data.entities.DuaName
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaNameDao {

    // Existing methods...
    @Query("SELECT * FROM duanames ORDER BY chap_id ASC")
    fun getAllDuaNames(): Flow<List<DuaName>>

    @Query("SELECT * FROM duanames WHERE category = :category ORDER BY chap_id ASC")
    fun getDuaNamesByCategory(category: String): Flow<List<DuaName>>

    @Query("SELECT * FROM duanames WHERE chap_id = :globalId")
    suspend fun getDuaNameByGlobalId(globalId: String): DuaName?

    @Query("SELECT * FROM duanames WHERE chapname LIKE '%' || :query || '%' ORDER BY chap_id ASC")
    fun searchDuaNames(query: String): Flow<List<DuaName>>

    @Query("SELECT DISTINCT chapname FROM duanames ORDER BY chapname ASC")
    fun getAllChapterNames(): Flow<List<String>>

    // NEW: Get DuaNames with actual category names joined
    @Query("""
        SELECT d.chap_id, d.chapname, c.name as category 
        FROM duanames d 
        LEFT JOIN category c ON d.category = c.id 
        ORDER BY d.chap_id ASC
    """)
    fun getAllDuaNamesWithCategoryNames(): Flow<List<DuaName>>

    @Query("""
        SELECT d.chap_id, d.chapname, c.name as category 
        FROM duanames d 
        LEFT JOIN category c ON d.category = c.id 
        WHERE d.category = :categoryId
        ORDER BY d.chap_id ASC
    """)
    fun getDuaNamesByCategoryId(categoryId: String): Flow<List<DuaName>>

    @Query("""
        SELECT d.chap_id, d.chapname, c.name as category 
        FROM duanames d 
        LEFT JOIN category c ON d.category = c.id 
        WHERE d.chapname LIKE '%' || :query || '%' 
        ORDER BY d.chap_id ASC
    """)
    fun searchDuaNamesWithCategoryNames(query: String): Flow<List<DuaName>>
}