import kr.entree.spigradle.kotlin.*

plugins {
    id("com.github.johnrengelman.shadow") version ("7.0.0")
    id("kr.entree.spigradle") version ("2.2.4")
    kotlin("jvm") version ("1.5.21")
}

group = "io.github.awesomemoder316.moderslib"
version = "1.17.1-3"

repositories {
    codemc()
    mavenCentral()
    spigotmc()
    sonatype()
    maven { url = uri("https://repo.mattstudios.me/artifactory/public/") }
}

dependencies {
    implementation("dev.triumphteam:triumph-gui:3.1.2")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0")
    implementation("net.wesjd:anvilgui:1.5.3-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")

    relocate("dev.triumphteam", "io.github.awesomemoder316.lib.api")
    relocate("kotlin", "io.github.awesomemoder316.lib.dependencies")
    relocate("net.kyori", "io.github.awesomemoder316.lib.api")
    relocate("net.wesjd", "io.github.awesomemoder316.lib.api")
    relocate("org.bstats", "io.github.awesomemoder316.lib.api")
}

spigot {
    authors = listOf("Awesomemoder316")
    apiVersion = "1.14"
    description = "A library of dependencies for other plugins!"
    website = "https://github.com/awesomemoder316/ImprovedManhunt"

    commands {
         create("moderslib") {
             description = "A command to view updates for plugins that use ModersLib."
             usage = "/moderslib"
         }
     }

    permissions {
        create("moderslib.receivepluginupdates") {
            description = "Permission to view updates managed by ModersLib."
            defaults = "op"
        }
    }
}
