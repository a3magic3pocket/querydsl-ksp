plugins {
	kotlin("jvm") version "2.0.21"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	`maven-publish`
}

repositories {
	mavenCentral()
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

sourceSets.main {
	kotlin.srcDir("src")
	resources.srcDir("resources")
}

sourceSets.test {
	kotlin.srcDir("test")
}

dependencies {
	implementation("com.google.devtools.ksp:symbol-processing-api:2.0.21-1.0.26")
	implementation("com.squareup:kotlinpoet:1.18.1")
	implementation("com.squareup:kotlinpoet-ksp:1.18.1")
	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
	implementation("com.querydsl:querydsl-core:5.1.0")
}

afterEvaluate {
	publishing {
		publications {
			create<MavenPublication>("maven") {
				from(components["kotlin"])
				groupId = "io.github.iceblizz6"
				artifactId = "querydsl-ksp"
				version = "0.0.1"
			}
		}
	}
}
