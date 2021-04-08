package com.tm.rankme.e2e

import org.springframework.stereotype.Component

@Component
class Memory {
    val items: MutableMap<String, Int> = mutableMapOf()
}