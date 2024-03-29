plugins {
    kotlin("jvm") version "1.8.10" apply false
    kotlin("plugin.spring") version "1.8.10" apply false
    kotlin("plugin.jpa") version "1.8.10" apply false
    id("org.springframework.boot") version "2.5.5" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.sonarqube") version "2.7.1"
}

allprojects {
    group = "com.tm.rankme"
    version = "0.84-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
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
    set("graphqlVersion", "8.1.1")
    set("graphiqlVersion", "8.1.1")
    set("graphqlJavaToolsVersion", "6.3.0")
}
