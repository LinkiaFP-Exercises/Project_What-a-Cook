plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.whatacook"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

fun loadEnv() {
    val envFile = file("../.env")
    if (envFile.exists()) {
        println("Loading environment variables from ${envFile.absolutePath}")
        envFile.forEachLine { line ->
            val parts = line.split("=")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
                println("Setting $key=$value")
                System.setProperty(key, value)
            }
        }
    } else {
        println("Env file not found: ${envFile.absolutePath}")
    }
}

tasks.test {
    useJUnitPlatform()
    doFirst {
        loadEnv()
    }
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Djdk.instrument.traceUsage=false")
}

// force unit testing before generating JAR
tasks.register<Jar>("customJar") {
    dependsOn("test")
    archiveClassifier.set("custom")
    from(sourceSets["main"].output)
}
