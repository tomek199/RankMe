package com.tm.rankme.query

import graphql.relay.*

data class Page<T>(
    val items: List<Item<T>>,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)

data class Item<T>(
    val node: T,
    val cursor: String
)

class ConnectionBuilder<T>(private val page: Page<T>) {

    fun build(): DefaultConnection<T> = DefaultConnection(edges(page), pageInfo(page))

    private fun edges(page: Page<T>): List<Edge<T>> = page.items.map {
        DefaultEdge(it.node, DefaultConnectionCursor(it.cursor))
    }

    private fun pageInfo(page: Page<T>): PageInfo = DefaultPageInfo(
        if (page.items.isNotEmpty()) DefaultConnectionCursor(page.items.first().cursor) else null,
        if (page.items.isNotEmpty()) DefaultConnectionCursor(page.items.last().cursor) else null,
        page.hasPreviousPage, page.hasNextPage
    )
}