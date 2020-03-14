import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

plugins {
    kotlin("jvm") version "1.3.70"
    jacoco
    id("org.sonarqube") version("2.7.1")
}

allprojects {
    group = "com.tm.rankme"
    version = "0.8-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    sonarqube { }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testCompile("org.junit.jupiter:junit-jupiter:5.5.2")
        testCompile("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
        testCompile("org.mockito:mockito-junit-jupiter:3.2.4")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
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
