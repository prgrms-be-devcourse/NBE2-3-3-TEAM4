import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

group = "com.team4"
version = "0.0.1-SNAPSHOT"

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.6")

    // MariaDB
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.2.0")

    // Security
    implementation ("org.springframework.boot:spring-boot-starter-security")

    //jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // json
    implementation ("org.json:json:20210307")

    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf:3.3.6")

    // Kotlin reflection features
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
