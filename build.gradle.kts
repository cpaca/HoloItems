plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" // Generates plugin.yml
    id("com.github.johnrengelman.shadow") version "8.1.1" // Shades and relocates dependencies into our plugin jar
    id("xyz.jpenilla.run-paper") version "2.3.0" // Adds runServer and runMojangMappedServer tasks for testing
}

group = "xyz.holocons.mc"
version = "0.0.1"
description = "A plugin for HoloCons SMP that adds a ton of custom items and blocks with unique abilities."

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.13")
}

tasks {
    // Configure shadowJar to run when invoking the build task
    assemble {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    // Configure the name of the unshaded jar
    jar {
        archiveClassifier.set("incomplete")
        archiveVersion.set("")
    }

    // Shade and relocate dependencies
    // https://github.com/johnrengelman/shadow
    shadowJar {
        relocate("com.github.stefvanschie.inventoryframework", "shadow.inventoryframework")

        archiveClassifier.set("")
    }

    // Configure the Minecraft version for runServer task
    // https://github.com/jpenilla/run-paper
    runServer {
        minecraftVersion("1.20.1")
    }
}

// Configure plugin.yml generation
// https://github.com/Minecrell/plugin-yml
bukkit {
    main = "xyz.holocons.mc.holoitemsrevamp.HoloItemsRevamp"
    apiVersion = "1.20"
    authors = listOf("TraceL", "dlee13")
    website = "holocons.xyz"
    depend = listOf("ProtocolLib")
    softDepend = listOf("WorldGuard")
    prefix = "HoloItems"

    commands {
        register("holoitems") {
            usage = "/holoitems"
        }
    }
}
