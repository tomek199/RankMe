package com.tm.rankme.application

import org.mockito.Mockito

fun <T> anyClass(type: Class<T>): T = Mockito.any(type)
