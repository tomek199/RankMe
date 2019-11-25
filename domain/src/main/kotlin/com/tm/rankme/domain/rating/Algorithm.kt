package com.tm.rankme.domain.rating

interface Algorithm {
    fun playerOneRating(): Int
    fun playerTwoRating(): Int
    fun playerOneDeviation(): Int
    fun playerTwoDeviation(): Int
}