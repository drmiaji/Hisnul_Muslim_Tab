package com.drmiaji.hisnulmuslimtab.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteChapter(
    @PrimaryKey val chapId: Int,
    val isFavorite: Boolean = true,
    val addedAt: Long = System.currentTimeMillis()
)