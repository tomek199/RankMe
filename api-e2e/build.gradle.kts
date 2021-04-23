import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion
val cucumberVersion = "6.10.2"
val graphqlKotlinVersion = "4.0.0-rc.1" // FIXME change to stable version when available

dependencies {
    testImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-spring:$cucumberVersion")

    testImplementation("com.expediagroup:graphql-kotlin-ktor-client:$graphqlKotlinVersion")
    testImplementation("com.expediagroup:graphql-kotlin-client-jackson:$graphqlKotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.4.4")
    testImplementation("org.testcontainers:junit-jupiter:1.15.3")
}
