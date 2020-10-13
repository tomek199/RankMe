package com.tm.rankme.application.common

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.LocalDate
import java.time.format.DateTimeParseException


class LocalDateCoercing : Coercing<LocalDate?, String?> {
    override fun parseValue(input: Any?): LocalDate? {
        if (input !is String) return null
        try {
            return LocalDate.parse(input)
        } catch (e: DateTimeParseException) {
            throw CoercingParseValueException("Could not parse value $input for LocalDate")
        }
    }

    override fun parseLiteral(input: Any?): LocalDate? {
        if (input !is StringValue) return null
        try {
            return LocalDate.parse(input.value)
        } catch (e: DateTimeParseException) {
            throw CoercingParseLiteralException("Could not parse literal $input for LocalDate")
        }
    }

    override fun serialize(dataFetcherResult: Any?): String? {
        when (dataFetcherResult) {
            is LocalDate -> return dataFetcherResult.toString()
            is String ->
                try {
                    return LocalDate.parse(dataFetcherResult).toString()
                } catch (e: DateTimeParseException) {
                    throw CoercingSerializeException("Could not serialize object $dataFetcherResult")
                }
        }
        return null
    }
}