plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("plugin.spring") version "1.4.30"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:8.0.0")
    implementation("com.graphql-java-kickstart:graphql-java-tools:6.2.0")
    implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:8.0.0")

    testImplementation("com.ninja-squad:springmockk:2.0.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
    testImplementation("com.graphql-java-kickstart:graphql-spring-boot-starter-test:8.0.0") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
}

springBoot {
    buildInfo()
}