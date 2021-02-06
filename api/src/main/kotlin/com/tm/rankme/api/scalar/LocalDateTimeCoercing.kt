package com.tm.rankme.api.scalar

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class LocalDateTimeCoercing : Coercing<LocalDateTime?, String?> {
    override fun parseValue(input: Any?): LocalDateTime? {
        if (input !is String) return null
        try {
            return LocalDateTime.parse(input)
        } catch (e: DateTimeParseException) {
            throw CoercingParseValueException("Could not parse value $input for LocalDateTime")
        }
    }

    override fun parseLiteral(input: Any?): LocalDateTime? {
        if (input !is StringValue) return null
        try {
            return LocalDateTime.parse(input.value)
        } catch (e: DateTimeParseException) {
            throw CoercingParseLiteralException("Could not parse literal $input for LocalDateTime")
        }
    }

    override fun serialize(dataFetcherResult: Any?): String? {
        when (dataFetcherResult) {
            is LocalDateTime -> return dataFetcherResult.toString()
            is String ->
                try {
                    return LocalDateTime.parse(dataFetcherResult).toString()
                } catch (e: DateTimeParseException) {
                    throw CoercingSerializeException("Could not serialize object $dataFetcherResult")
                }
        }
        return null
    }
}