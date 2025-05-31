package com.drmiaji.hisnulmuslimtab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drmiaji.hisnulmuslimtab.data.entities.FavoriteChapter
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteChapter>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE chapId = :chapId)")
    suspend fun isFavorite(chapId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteChapter)

    @Query("DELETE FROM favorites WHERE chapId = :chapId")
    suspend fun deleteFavorite(chapId: Int)
}