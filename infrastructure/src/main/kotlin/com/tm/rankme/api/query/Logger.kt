package com.tm.rankme.api.query

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T : Any> logger(): Logger = LoggerFactory.getLogger(T::class.java)