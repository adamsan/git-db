import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.3.72"
}

group = "hu.adamsan"
version = "0.0.2-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.xerial", "sqlite-jdbc", "3.32.3.2")
    implementation("org.jdbi:jdbi3-core:3.17.0")
    implementation("org.jdbi:jdbi3-sqlite:3.17.0")
    implementation( "org.slf4j:slf4j-simple:1.7.30")

    testImplementation("org.mockito:mockito-inline:3.6.0")
    testImplementation("org.hamcrest:hamcrest-library:2.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
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
