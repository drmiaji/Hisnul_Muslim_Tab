package com.drmiaji.hisnulmuslimtab.data.dao

import androidx.room.*
import com.drmiaji.hisnulmuslimtab.data.entities.DuaDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface DuaDetailDao {
    // dua_global_id is Int, not String
    @Query("SELECT * FROM duadetails WHERE dua_global_id = :globalId ORDER BY id")
    fun getDuaDetailsByGlobalId(globalId: Int): Flow<List<DuaDetail>>

    // Column is 'id' not 'ID'
    @Query("SELECT * FROM duadetails WHERE id = :id")
    suspend fun getDuaDetailById(id: Int): DuaDetail?

    @Query("SELECT * FROM duadetails WHERE arabic LIKE '%' || :searchQuery || '%' OR transliteration LIKE '%' || :searchQuery || '%' OR translations LIKE '%' || :searchQuery || '%'")
    fun searchDuaDetails(searchQuery: String): Flow<List<DuaDetail>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuaDetail(duaDetail: DuaDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuaDetails(duaDetails: List<DuaDetail>)

    @Query("SELECT * FROM duadetails ORDER BY id ASC")
    fun getAllDuaDetailsSorted(): Flow<List<DuaDetail>>
}