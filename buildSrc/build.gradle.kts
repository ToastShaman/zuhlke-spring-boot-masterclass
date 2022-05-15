import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("org.flywaydb:flyway-core:8.5.10")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.jooq:jooq-codegen:3.14.15")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
