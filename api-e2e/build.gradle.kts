import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    kotlin("jvm")
    id("se.thinkcode.cucumber-runner") version "0.0.11"
}

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_11

val kotlinVersion = project.getKotlinPluginVersion()
val cucumberVersion = "7.3.4"
val graphqlKotlinVersion = "5.5.0"
val springBootVersion = "2.7.0"

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
    testImplementation("org.testcontainers:junit-jupiter:1.17.2")
}

tasks.test {
    enabled = false
}

task<Test>("e2e") {
    description = "Runs end-to-end tests."
    group = "verification"

    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11
}
