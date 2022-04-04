package com.tm.rankme.e2e.query

class RequestCursor(direction: Direction, val value: String) {
    val direction: String = direction.name.lowercase()

    enum class Direction {
        BEFORE, AFTER
    }
}
