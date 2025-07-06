plugins {
    java
    `java-library`
    `maven-publish`
    id("io.github.crimix.replace-placeholders") version "3.0"
}

val appName = project.property("app_name").toString()
val mainClassName = project.property("main_class").toString()
val appVersion = project.property("app_version")!!

group = "dev.bamboobear"
version = appVersion

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.gson:gson:${project.property("gson_version")}") {
        exclude("com.google.errorprone", "error_prone_annotations")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    archiveBaseName = appName
    destinationDirectory = layout.buildDirectory

    manifest {
        attributes ( mapOf(
            "Main-Class" to mainClassName,
            "Class-Path" to project.configurations.runtimeClasspath.get()
                .joinToString(" ") { "libs/${it.name}" },
            "Implementation-Title" to appName,
            "Implementation-Version" to project.version
        ))
    }
}

replaceSourcePlaceholders {
    val mainClass = mainClassName.replace('.', '/')

    enabled(true)
    filesToExpand("${mainClass}.java")
    extraProperties("app_version")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name = appName
            }
        }
    }
}

tasks.register<Copy>("copyDependencies") {
    from(project.configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("libs"))
}

tasks.named<Jar>("sourcesJar") {
    destinationDirectory.set(layout.buildDirectory)
}

tasks.named<Jar>("javadocJar") {
    destinationDirectory.set(layout.buildDirectory)
}

tasks.register<JavaExec>("run") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = mainClassName
}

tasks["build"].dependsOn("copyDependencies")
