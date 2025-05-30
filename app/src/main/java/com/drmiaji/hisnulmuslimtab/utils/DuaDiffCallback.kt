package com.drmiaji.hisnulmuslimtab.utils

import androidx.recyclerview.widget.DiffUtil
import com.drmiaji.hisnulmuslimtab.data.entities.DuaName

class DuaDiffCallback(
    private val oldList: List<DuaName>,
    private val newList: List<DuaName>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].chap_id == newList[newItemPosition].chap_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}