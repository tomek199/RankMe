package com.tm.rankme.api.scalar

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.util.*


class UUIDCoercing : Coercing<UUID?, String?> {
    override fun parseValue(input: Any?): UUID? {
        if (input !is String) return null
        try {
            return UUID.fromString(input)
        } catch (e: IllegalArgumentException) {
            throw CoercingParseValueException("Could not parse value $input for UUID", e)
        }
    }

    override fun parseLiteral(input: Any?): UUID? {
        if (input !is StringValue) return null
        try {
            return UUID.fromString(input.value)
        } catch (e: IllegalArgumentException) {
            throw CoercingParseLiteralException("Could not parse literal $input for UUID", e)
        }
    }

    override fun serialize(dataFetcherResult: Any?): String? {
        when (dataFetcherResult) {
            is UUID -> return dataFetcherResult.toString()
            is String ->
                try {
                    return UUID.fromString(dataFetcherResult).toString()
                } catch (e: IllegalArgumentException) {
                    throw CoercingSerializeException("Could not serialize object $dataFetcherResult", e)
                }
        }
        return null
    }
}