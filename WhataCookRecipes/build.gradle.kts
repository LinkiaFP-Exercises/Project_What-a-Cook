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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.3.0") {
        exclude(group = "javax.annotation", module = "javax.annotation-api")
    }
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.0") {
        exclude(group = "javax.annotation", module = "javax.annotation-api")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation") {
        exclude(group = "javax.annotation", module = "javax.annotation-api")
    }

    // Declarar las dependencias transitivas directamente
    implementation("io.projectreactor:reactor-core:3.6.6") {
        exclude(group = "javax.annotation", module = "javax.annotation-api")
    }
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.3.0")
    implementation("org.springframework.boot:spring-boot:3.3.0")
    implementation("org.springframework.data:spring-data-commons:3.3.0")
    implementation("org.springframework.data:spring-data-mongodb:4.3.0")
    implementation("org.springframework:spring-context:6.1.8")
    implementation("org.springframework:spring-web:6.1.8")

    // Asegurarse de incluir la dependencia de jakarta.annotation
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")

    // Development only
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Dependencias de prueba
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mongodb:mongodb-driver-reactivestreams:5.0.1")
    testImplementation("org.reactivestreams:reactive-streams:1.0.4")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.3.0")
    testImplementation("org.springframework.boot:spring-boot-test:3.3.0")
    testImplementation("org.springframework:spring-beans:6.1.8")
    testImplementation("org.springframework:spring-test:6.1.8")
    testImplementation("org.springframework:spring-webflux:6.1.8")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.autonomousapps.dependency-analysis") version "1.32.0"
}

// Configure Javadoc task
tasks.withType<Javadoc> {
    // Configura opciones específicas de Javadoc
    options {
        encoding = "UTF-8"
        (this as StandardJavadocDocletOptions).apply {
            windowTitle = "What-a-Cook #RECIPES API Documentation"
            docTitle = "What-a-Cook #RECIPES API Documentation - v1.0"
            addStringOption("Xdoclint:none", "-quiet")
        }
    }

    // Excluir archivos específicos que causan problemas
    exclude("**/org/springframework/lang/Nullable.java")
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

tasks.test {
    useJUnitPlatform()
    doFirst {
        loadEnv()
        environment("SPRING_PROFILES_ACTIVE", "test")
        environment("MONGO_URI_WHATACOOK_RECIPES", System.getProperty("MONGO_URI_WHATACOOK_RECIPES"))
    }
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Djdk.instrument.traceUsage=false")
}

// Force unit testing before generating JAR
tasks.register<Jar>("customJar") {
    dependsOn("test")
    archiveClassifier.set("custom")
    from(sourceSets["main"].output)
}
