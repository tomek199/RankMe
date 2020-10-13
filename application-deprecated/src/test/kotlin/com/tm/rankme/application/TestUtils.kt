package com.tm.rankme.application

import org.mockito.Mockito

fun <T> any(type: Class<T>): T = Mockito.any(type)
