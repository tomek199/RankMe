plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("plugin.spring") version "1.3.72"
}

dependencies {
    compile(project(":domain"))
    compile(project(":infrastructure"))

    compile("com.graphql-java:graphql-spring-boot-starter:5.0.2")
    compile("com.graphql-java:graphql-java-tools:5.2.4")
    compile("com.graphql-java:graphiql-spring-boot-starter:5.0.2")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
