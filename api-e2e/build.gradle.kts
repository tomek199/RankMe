plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.cucumber:cucumber-java8:6.10.2")
    testImplementation("io.cucumber:cucumber-junit:6.10.2")
    testImplementation("io.cucumber:cucumber-spring:6.10.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.4.4")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${plugins.getPlugin(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class.java).kotlinPluginVersion}")
}
