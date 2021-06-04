package com.tm.rankme.e2e.steps

import com.tm.rankme.e2e.db.DatabaseUtil
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class CommonSteps(private val dbUtil: DatabaseUtil) : En {
    init {
        Then("I cleanup database") {
            runBlocking {
                delay(1000)
                dbUtil.cleanup()
            }
        }
    }
}

const val status = "SUBMITTED"