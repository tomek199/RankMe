package com.tm.rankme.e2e.steps

import com.tm.rankme.e2e.util.DatabaseUtil
import io.cucumber.java8.En
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value

class CommonSteps(
    private val dbUtil: DatabaseUtil,
    @Value("\${cucumber.step-delay}") private val stepDelay: Long
) : En {
    init {
        Then("I cleanup database") {
            runBlocking {
                delay(stepDelay)
                dbUtil.cleanup()
                delay(stepDelay)
            }
        }
    }
}

const val status = "SUBMITTED"