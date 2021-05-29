package com.tm.rankme.model

data class Page<T>(
    val items: List<Item<T>>,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)

data class Item<T>(
    val node: T,
    val cursor: String
)