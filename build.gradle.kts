plugins {
    id("com.github.johnrengelman.shadow") version ("7.0.0")
    kotlin("jvm") version ("1.5.21")
}

group = "me.awesomemoder316.moderslib"
version = "1.17.1-1"

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        isAllowInsecureProtocol = true
    }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/")
        isAllowInsecureProtocol = true
    }
    maven { url = uri("https://repo.mattstudios.me/artifactory/public/")
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib")) //Included here instead of Library in plugin.yml cause of 1.14.2 not supporting Library
    implementation("dev.triumphteam:triumph-gui:3.0.3")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.5.2-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

artifacts.archives(tasks.shadowJar)

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-" + rootProject.version + ".jar")

    relocate("kotlin", "me.awesomemoder316.lib.dependencies")
    relocate("dev.triumphteam", "me.awesomemoder316.lib.api")
    relocate("net.kyori", "me.awesomemoder316.lib.api")
    relocate("net.wesjd", "me.awesomemoder316.lib.api")
    relocate("org.bstats", "me.awesomemoder316.lib.api")
}
