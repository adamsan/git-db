import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
}

group = "hu.adamsan"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.xerial", "sqlite-jdbc", "3.32.3.2")
	implementation("org.jdbi:jdbi3-core:3.17.0")
	implementation("org.jdbi:jdbi3-sqlite:3.17.0")
	testImplementation ("org.mockito:mockito-inline:3.6.0")
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
	from(file("$buildDir/libs/gitdb-0.0.1-SNAPSHOT.jar"))
	from(file("$buildDir/resources/main/scripts/gitdb"))
	from(file("$buildDir/resources/main/scripts/gitdb.bat"))
	into(file(System.getenv("GITDB_HOME")))
}
