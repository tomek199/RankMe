plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("plugin.spring") version "1.3.72"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))

    implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:7.1.0")
    implementation("com.graphql-java-kickstart:graphql-java-tools:6.1.0")
    implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:7.1.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
