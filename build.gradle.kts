import jooq.generate.JooqGeneratorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id("com.gorylenko.gradle-git-properties") version "2.3.2"
}

group = "com.github.toastshaman"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-json-org")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("dev.forkhandles:result4k")
    implementation("dev.forkhandles:values4k")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.0")
    implementation("org.bitbucket.b_c:jose4j:0.7.12")
    implementation("org.flywaydb:flyway-core")

    @Suppress("GradlePackageUpdate")
    runtimeOnly("com.h2database:h2:1.4.200")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")
    testImplementation("com.approvaltests:approvaltests:15.2.0")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("io.mockk:mockk:1.12.3")

    modules {
        module("org.springframework.boot:spring-boot-starter-tomcat") {
            replacedBy("org.springframework.boot:spring-boot-starter-undertow", "Use Undertow instead of Tomcat")
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.1")
        mavenBom("org.testcontainers:testcontainers-bom:1.16.2")
        mavenBom("dev.forkhandles:forkhandles-bom:2.1.1.0")
        mavenBom("com.squareup.okhttp3:okhttp-bom:4.9.3")
    }
}

tasks.withType<KotlinCompile> {
    dependsOn("generate-sources")

    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    main {
        java {
            srcDir("${buildDir}/generated/sources/jooq")
        }
    }
}

tasks.register<JooqGeneratorTask>("generate-sources") {
    migrationLocations.set("filesystem:${project.projectDir}/src/main/resources/db/migration")
    outputPackageName.set("com.github.toastshaman.springbootmasterclass.todo.jooq")
    outputDirectory.set(layout.buildDirectory.dir("generated/sources/jooq"))
}
