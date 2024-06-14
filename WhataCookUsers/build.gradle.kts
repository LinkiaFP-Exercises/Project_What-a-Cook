group = "com.whatacook"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.0")
    implementation("io.jsonwebtoken:jjwt:0.12.5")

    // Declarar las dependencias transitivas directamente
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.4")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.projectreactor:reactor-core:3.6.3")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
    implementation("org.eclipse.angus:jakarta.mail:2.0.2")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.2.3")
    implementation("org.springframework.boot:spring-boot:3.2.3")
    implementation("org.springframework.data:spring-data-commons:3.2.3")
    implementation("org.springframework.data:spring-data-mongodb:4.2.3")
    implementation("org.springframework.security:spring-security-config:6.2.2")
    implementation("org.springframework.security:spring-security-core:6.3.0")
    implementation("org.springframework.security:spring-security-crypto:6.2.2")
    implementation("org.springframework.security:spring-security-web:6.2.2")
    implementation("org.springframework:spring-beans:6.1.4")
    implementation("org.springframework:spring-context-support:6.1.4")
    implementation("org.springframework:spring-context:6.1.4")
    implementation("org.springframework:spring-core:6.1.4")
    implementation("org.springframework:spring-tx:6.1.4")
    implementation("org.springframework:spring-web:6.1.8")
    implementation("org.springframework:spring-webflux:6.1.4")

    // Dependencias de prueba
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mongodb:mongodb-driver-reactivestreams:4.11.1")
    testImplementation("org.springframework.boot:spring-boot-test:3.2.3")
    testImplementation("org.springframework:spring-test:6.1.4")
    testImplementation("commons-io:commons-io:2.8.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Development only
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.autonomousapps.dependency-analysis") version "1.32.0"
}

// Configure Javadoc task
tasks.withType<Javadoc> {
    options {
        encoding = "UTF-8"
        (this as StandardJavadocDocletOptions).apply {
            windowTitle = "What-a-Cook #RECIPES API Documentation"
            docTitle = "What-a-Cook #RECIPES API Documentation - v1.0"
        }
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.netty") {
            useVersion("4.1.108.Final") // Fix CVE-2024-29025 Vulnerability
        }
    }
}

// Function that loads environment variables from the .env file
fun loadEnv() {
    val envFile = file("../.env")
    if (envFile.exists()) {
        println("Loading environment variables from ${envFile.absolutePath}")
        envFile.forEachLine { line ->
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
                // println("Setting $key=$value")
                println("Setting $key")
                System.setProperty(key, value)
            }
        }
    } else {
        println("Env file not found: ${envFile.absolutePath}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    doFirst {
        loadEnv()
        environment("SPRING_PROFILES_ACTIVE", "test")
        environment("GMAIL_APP_PASSWORD", System.getProperty("GMAIL_APP_PASSWORD"))
        environment("JWT_SECRET", System.getProperty("JWT_SECRET"))
        environment("MONGO_URI_WHATACOOK_USERS", System.getProperty("MONGO_URI_WHATACOOK_USERS"))
        environment("SPRING_MAIL_VALIDATION", System.getProperty("SPRING_MAIL_VALIDATION"))
    }
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Djdk.instrument.traceUsage=false")
}

// Force unit testing before generating JAR
tasks.register<Jar>("customJar") {
    dependsOn("test")
    archiveClassifier.set("custom")
    from(sourceSets["main"].output)
}
