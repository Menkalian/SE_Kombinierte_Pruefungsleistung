plugins {
    java
    kotlin("jvm") version "1.4.10"
}

group = "de.dhbw.mosbach"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
