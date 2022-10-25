plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("org.sonarqube") version "2.7.1" apply false
    id("org.springframework.boot") version "2.5.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("plugin.spring") version "1.5.21" apply false
    kotlin("plugin.jpa") version "1.5.21" apply false
}

allprojects {
    group = "com.tm.rankme"
    version = "0.77-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
}

ext {
    set("springCloudVersion", "2020.0.4")
    set("jnanoidVersion", "2.0.0")
    set("jacksonModuleKotlinVersion", "2.11.3")
    set("junitJupiterVersion", "5.8.1")
    set("mockkVersion", "1.12.0")
    set("springmockkVersion", "3.0.1")
    set("jacocoVersion", "0.8.7")
}
