package com.tm.rankme.api.query

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

abstract class Mapper<R> {
    protected val objectMapper: ObjectMapper = jacksonObjectMapper().findAndRegisterModules()

    abstract fun deserialize(data: ByteArray): R
}