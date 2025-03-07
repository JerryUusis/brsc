plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "org"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core") // https://spring.io/guides/tutorials/spring-boot-kotlin Check Exposing HTTP API
    }
    testImplementation("com.ninja-squad:springmockk:4.0.2") // https://github.com/Ninja-Squad/springmockk
    testImplementation("io.mockk:mockk:1.13.16") // https://mockk.io/
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql") // https://github.com/jwtk/jjwt?tab=readme-ov-file#gradle
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    testImplementation("org.springframework.security:spring-security-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    testLogging {
        events("passed", "skipped", "failed") // configures test output to show passed, skipped, and failed tests.

        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
        showStandardStreams = false // Set to `true` for verbose output
    }
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    environment("SPRING_PROFILE_ACTIVE", "dev")
    jvmArgs = listOf("-Dspring.profiles.active=dev")
}

// https://kotlinlang.org/docs/gradle-configure-project.html
// https://docs.gradle.org/current/userguide/build_environment.html
// Will build and launch using spring profile "dev"
tasks.register<JavaExec>("runDev") {
    group = "application"
    mainClass.set("org.testing_survey_creator.TestingSurveyCreatorApplicationKt") // In Kotlin, when you define a main function at the top level (outside of any class), the Kotlin compiler generates a class named after the file, appending Kt to the filename.
    environment("SPRING_PROFILES_ACTIVE", "dev")
    classpath = sourceSets["main"].runtimeClasspath
    dependsOn("build")
}