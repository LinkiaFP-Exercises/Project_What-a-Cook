plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

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
    implementation ("org.springframework.boot:spring-boot-starter-mail:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt:0.12.5")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("commons-io:commons-io:2.8.0")
}

// function that loads environment variables from the .env file
fun loadEnv() {
    val envFile = file("../.env")
    if (envFile.exists()) {
        println("Loading environment variables from ${envFile.absolutePath}")
        envFile.forEachLine { line ->
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
//                println("Setting $key=$value")
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

// force unit testing before generating JAR
tasks.register<Jar>("customJar") {
    dependsOn("test")
    archiveClassifier.set("custom")
    from(sourceSets["main"].output)
}
