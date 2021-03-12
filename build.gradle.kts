plugins {
    kotlin("jvm") version "1.4.30" apply false
    id("org.sonarqube") version "2.7.1"
}

allprojects {
    group = "com.tm.rankme"
    version = "0.44-SNAPSHOT"
}
