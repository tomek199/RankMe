package com.tm.rankme.infrastructure

import java.util.*

fun decode(value: String): Long = String(Base64.getDecoder().decode(value)).toLong()

fun encode(timestamp: Long): String = Base64.getEncoder().encodeToString(timestamp.toString().toByteArray())