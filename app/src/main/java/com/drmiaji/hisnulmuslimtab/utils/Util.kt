package com.drmiaji.hisnulmuslimtab.utils

fun Int.toBengaliNumberString(): String {
    val bengaliDigits = listOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
    return this.toString().map { if (it.isDigit()) bengaliDigits[it.digitToInt()] else it }.joinToString("")
}