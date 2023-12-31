import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1" // Kotlin code linter
    id("io.gitlab.arturbosch.detekt") version "1.23.1" // static code analysis tool
    id("com.diffplug.spotless") version "6.21.0" // code formatter
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
}

group = "lt.homeassignment"
version = "latest"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

// TODO exclude unneeded dependencies
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate.validator:hibernate-validator:7.0.2.Final")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:1.7.0")
    implementation("io.github.resilience4j:resilience4j-retry:1.7.0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("net.logstash.logback:logstash-logback-encoder:6.6")
    implementation("ch.qos.logback:logback-classic:1.4.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.12.0")
    // Cucumber dependencies
    testImplementation("io.cucumber:cucumber-java:6.11.0")
    testImplementation("io.cucumber:cucumber-junit:6.11.0")
    testImplementation("io.cucumber:cucumber-spring:6.11.0")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("verification") {
    dependsOn("test", "ktlintCheck", "detekt", "spotlessCheck")
}
