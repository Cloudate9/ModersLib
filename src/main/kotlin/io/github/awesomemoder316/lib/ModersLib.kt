package io.github.awesomemoder316.lib

import io.github.awesomemoder316.lib.api.ModersLibApi
import io.github.awesomemoder316.lib.command.ViewUpdates
import io.github.awesomemoder316.lib.listeners.PlayerJoin
import io.github.awesomemoder316.lib.utils.GeneralUtils
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL

class ModersLib: JavaPlugin() {

    private var adventure: BukkitAudiences? = null

    override fun onEnable() {
        GeneralUtils.plugin = this

        adventure = BukkitAudiences.create(this)

        if (Bukkit.getBukkitVersion().split(".")[0] == "1") {
            when (Bukkit.getBukkitVersion().split(".")[1]) {
                "14", "15" -> {
                    GeneralUtils.pre116 = true
                    if (Bukkit.getBukkitVersion().split("-")[0] == "1.14.1") {
                        GeneralUtils.plugin.logger.severe("[ModersLib] The plugin \"ModersLib\" does not work on 1.14.1! Please update to Minecraft 1.14.2 or higher.")
                        Bukkit.getPluginManager().disablePlugin(this)
                        return
                    }
                }
            }
        }

        config.options().copyDefaults(false)
        config.options().copyHeader(true)
        saveConfig()

        Bukkit.getPluginManager().registerEvents(PlayerJoin(), this)

        getCommand("moderslib")?.setExecutor(ViewUpdates())

        ModersLibApi().githubUpdateCheck("awesomemoder316",
            "moderslib",
            URL("https://www.curseforge.com/minecraft/bukkit-plugins/moderslib"),
            this)
    }

    override fun onDisable() {
        adventure?.close() ?: return
        adventure = null
    }

}