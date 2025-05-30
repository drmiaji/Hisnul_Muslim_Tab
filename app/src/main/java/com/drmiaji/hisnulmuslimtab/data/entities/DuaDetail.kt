package com.drmiaji.hisnulmuslimtab.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duadetails")
data class DuaDetail(
    @PrimaryKey val id: Int,
    val arabic: String?,
    val arabic_diacless: String?,
    val bottom: String?,
    val dua_global_id: Int,
    val reference: String?,
    val top: String?,
    val translations: String?,
    val transliteration: String?
)