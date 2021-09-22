plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.netty:netty-all:4.1.68.Final")
}

//application {
//    // Define the main class for the application.
//    mainClass.set("com.network.AppKt")
//}
