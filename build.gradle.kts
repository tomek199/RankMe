import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

plugins {
    kotlin("jvm") version "1.4.10"
    jacoco
    id("org.sonarqube") version("2.7.1")
}

allprojects {
    group = "com.tm.rankme"
    version = "0.31-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    sonarqube { }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
        testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
        testImplementation("io.mockk:mockk:1.10.3")
    }

    tasks.register("stage") {
        dependsOn("build")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }
}

tasks.register("stage") {
    group = "Build"
    description = "Assembles and test this project for Heroku deployment"
    dependsOn("build")
}
