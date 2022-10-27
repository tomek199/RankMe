package com.tm.rankme.scalar

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class LocalDateTimeCoercing : Coercing<LocalDateTime, String?> {
    override fun parseValue(input: Any): LocalDateTime {
        if (input !is String) throw CoercingParseValueException("Incorrect value type ${input::class} for LocalDateTime")
        try {
            return LocalDateTime.parse(input)
        } catch (e: DateTimeParseException) {
            throw CoercingParseValueException("Could not parse value $input for LocalDateTime")
        }
    }

    override fun parseLiteral(input: Any): LocalDateTime {
        if (input !is StringValue) throw CoercingParseLiteralException("Incorrect literal type ${input::class} for LocalDateTime")
        try {
            return LocalDateTime.parse(input.value)
        } catch (e: DateTimeParseException) {
            throw CoercingParseLiteralException("Could not parse literal $input for LocalDateTime")
        }
    }

    override fun serialize(dataFetcherResult: Any): String {
        return when (dataFetcherResult) {
            is LocalDateTime -> dataFetcherResult.toString()
            is String ->
                try {
                    LocalDateTime.parse(dataFetcherResult).toString()
                } catch (e: DateTimeParseException) {
                    throw CoercingSerializeException("Could not serialize object $dataFetcherResult")
                }
            else -> throw CoercingSerializeException("Incorrect type to serialize ${dataFetcherResult::class}")
        }
    }
}