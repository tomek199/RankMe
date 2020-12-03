package com.tm.rankme.api.mutation

data class Result(val status: Status = Status.SUCCESS, val message: String? = null)

enum class Status {
    SUCCESS, FAILURE
}