import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
    id("se.thinkcode.cucumber-runner") version "0.0.9"
}

repositories {
    mavenCentral()
}

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion
val cucumberVersion = "6.10.2"
val graphqlKotlinVersion = "4.0.0-rc.1" // FIXME change to stable version when available
val springBootVersion = "2.4.4"

dependencies {
    testImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-spring:$cucumberVersion")

    testImplementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinVersion")
    testImplementation("com.expediagroup:graphql-kotlin-client-jackson:$graphqlKotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb:$springBootVersion")
    testImplementation("org.testcontainers:junit-jupiter:1.15.3")
}

tasks.test {
    enabled = false
}

task<Test>("e2e") {
    description = "Runs end-to-end tests."
    group = "verification"

    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
}
