@file:Suppress("GradleDependency", "UnstableApiUsage", "OldTargetApi")

/*
 * This file configures the build system that creates your Android app.
 * The syntax is Kotlin, not Java.
 * You do not need to understand the contents of this file, nor should you modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 */

plugins {
    id("com.android.application")
    id("org.cs124.gradlegrader") version "2024.10.0"
    checkstyle
}
android {
    namespace = "edu.illinois.cs.cs124.ay2024.mp"
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    defaultConfig {
        applicationId = "edu.illinois.cs.cs124.ay2024.joinable"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
/*
 * Do not add dependencies here, since they will be overwritten during official grading.
 * If you have a package that you think would be useful for completing the MP,
 please suggest it on the forum.
 */
dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.18.0")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.21"))

    testImplementation("org.cs124.gradlegrader:lib:2024.10.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("androidx.test.ext:truth:1.6.0")
    testImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("com.google.guava:guava:33.3.1-android")
}
checkstyle {
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    toolVersion = "10.18.2"
}
tasks.register("checkstyle", Checkstyle::class) {
    source("src/main/java")
    include("**/*.java")
    classpath = files()
}
gradlegrader {
    assignment = "AY2024.MP"
    points {
        total = 100
    }
    checkpoint {
        yamlFile = rootProject.file("grade.yaml")
        configureTests { checkpoint, test ->
            require(checkpoint in setOf("0", "1", "2", "3")) { "Cannot grade unknown checkpoint MP$checkpoint" }
            test.setTestNameIncludePatterns(listOf("MP${checkpoint}Test"))
            test.filter.isFailOnNoMatchingTests = true
        }
    }
    checkstyle {
        points = 10
    }
    earlyDeadline {
        points = { checkpoint ->
            when (checkpoint) {
                in setOf("2", "3") -> 10
                else -> 0
            }
        }
        noteForPoints = { checkpoint, points ->
            "Checkpoint $checkpoint has an early deadline, so the maximum local score is ${100 - points}/100.\n" +
                "$points points will be provided during official grading if you submit code " +
                "that meets the early deadline threshold before the early deadline."
        }
    }
    forceClean = false
    identification {
        txtFile = rootProject.file("ID.txt")
        validate =
            Spec {
                val id = it.trim()
                check(id.length == 27) { "Invalid ID: $id" }
                true
            }
    }
    reporting {
        post {
            endpoint = "https://cloud.cs124.org/gradlegrader"
        }
        printPretty {
            title = "Grade Summary"
        }
    }
    vcs {
        git = true
        requireCommit = true
    }
}
configurations.checkstyle {
    resolutionStrategy.capabilitiesResolution.withCapability("com.google.collections:google-collections") {
        select("com.google.guava:guava:0")
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
project.afterEvaluate {
    tasks.withType(Test::class.java).forEach { test ->
        // Allow security manager for MP2 and MP3
        test.jvmArgs("-Djava.security.manager=allow")
        // Silence deprecation warning regarding security manager
        test.logging.captureStandardError(LogLevel.DEBUG)
    }
}
