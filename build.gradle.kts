import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.7.10"
}

group = "hu.adamsan"
version = "0.0.2-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.xerial", "sqlite-jdbc", "3.32.3.2")
    implementation("org.jdbi:jdbi3-core:3.32.0")
    implementation("org.jdbi:jdbi3-sqlite:3.32.0")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.mockito:mockito-inline:4.6.1")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.register<Copy>("buildAndCopyJar") {
    dependsOn("build")
    dependsOn("assemble")
    from(file("$buildDir/libs/gitdb-$version.jar"))
    from(file("$buildDir/resources/main/scripts/gitdb"))
    from(file("$buildDir/resources/main/scripts/gitdb.bat"))
    from(file("$buildDir/resources/main/scripts/gitdblist"))
    from(file("$buildDir/resources/main/scripts/gitdblist.bat"))
    into(file(System.getenv("GITDB_HOME")))
}
