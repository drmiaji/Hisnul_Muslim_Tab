package com.drmiaji.hisnulmuslim.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duanames")
data class DuaName(
    @PrimaryKey val chap_id: Int,
    val category: String?,
    val chapname: String?
)