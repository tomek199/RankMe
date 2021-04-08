package com.tm.rankme.e2e

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

@CucumberContextConfiguration
@SpringBootTest(classes = [Runner::class])
@EnableAutoConfiguration
@ComponentScan("com.tm.rankme.e2e")
class SpringConfig