plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val javafxVersion = "21.0.4"
val jakartaMailVersion = "2.0.2"
val poiVersion = "5.4.1"
val flatlafVersion = "3.4.1"

dependencies {
    implementation("org.openjfx:javafx-controls:${javafxVersion}")
    implementation("org.openjfx:javafx-fxml:${javafxVersion}")
    implementation("com.sun.mail:jakarta.mail:${jakartaMailVersion}")
    implementation("org.apache.poi:poi:${poiVersion}")
    implementation("org.apache.poi:poi-ooxml:${poiVersion}")
    implementation("com.formdev:flatlaf:${flatlafVersion}")
    // https://mvnrepository.com/artifact/org.json/json
    implementation("org.json:json:20250517")
}


javafx {
    version = javafxVersion
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("com.yanuar.Main")
}
