dependencies {
    implementation(project(":domain"))
    implementation("org.springframework:spring-context:5.3.0")
    implementation("com.github.EventStore:EventStoreDB-Client-Java:trunk-SNAPSHOT")
    testImplementation("org.mockito:mockito-inline:3.3.3")
}