package com.tm.rankme.e2e.steps

import com.tm.rankme.e2e.Memory
import io.cucumber.java8.En
import kotlin.test.assertEquals

class CalculatorSteps(memory: Memory) : En {
    init {
        Given("I have numbers {int} and {int}") { number1: Int, number2: Int ->
            memory.items["number1"] = number1
            memory.items["number2"] = number2
        }

        When("I want to sum") {
            val number1 = memory.items["number1"]?: throw IllegalStateException("Number is invalid")
            val number2 = memory.items["number2"]?: throw  IllegalStateException("Number is invalid")
            memory.items["sum"] = number1 + number2
        }

        Then("I should have result {int}") { result: Int ->
            assertEquals(result, memory.items["sum"])
        }
    }
}