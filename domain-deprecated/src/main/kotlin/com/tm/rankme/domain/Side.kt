package com.tm.rankme.domain

data class Side<T>(
    val content: Collection<T>,
    val total: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean
)
