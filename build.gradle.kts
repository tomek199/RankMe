import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion

plugins {
    kotlin("jvm") version "1.3.60"
    jacoco
    id("org.sonarqube") version("2.7.1")
}

allprojects {
    group = "com.tm.rankme"
    version = "0.2-SNAPSHOT"
    
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    sonarqube {
        properties {
            property("sonar.sources", "src/main")
            property("sonar.tests", "src/test")
//            property("sonar.jacoco.reportPath", "build/jacoco/test.exec")
//            property("sonar.test.exclusions", "src/test")
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testCompile("org.junit.jupiter:junit-jupiter:5.5.2")
        testCompile("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.test {
        useJUnitPlatform()
    }
}

tasks.register("info") {
    doLast {
        println(project.base)
    }
}