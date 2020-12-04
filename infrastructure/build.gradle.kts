plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("plugin.spring") version "1.4.10"
}

dependencies {
    implementation(project(":application"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.1")

    implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:8.0.0")
    implementation("com.graphql-java-kickstart:graphql-java-tools:6.2.0")
    implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:8.0.0")

    implementation("com.github.EventStore:EventStoreDB-Client-Java:trunk-SNAPSHOT")

    testImplementation("org.mockito:mockito-inline:3.6.28")
    testImplementation("com.graphql-java-kickstart:graphql-spring-boot-starter-test:8.0.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

springBoot {
    buildInfo()
}
