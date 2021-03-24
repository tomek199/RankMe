package com.tm.rankme.projection

import java.util.function.Consumer

interface MessageConsumer<T> {
    fun consume(): Consumer<T>
}