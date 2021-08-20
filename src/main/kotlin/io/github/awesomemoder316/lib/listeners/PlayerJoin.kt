package io.github.awesomemoder316.lib.listeners

import io.github.awesomemoder316.lib.utils.GeneralUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin: Listener {

    @EventHandler(priority = EventPriority.LOWEST) //Ensures that this is sent last.
    fun onJoin(e: PlayerJoinEvent) {

        if (!e.player.hasPermission("moderslib.receivepluginupdates")) return

            if (GeneralUtils.updateDetected.isNotEmpty())

                e.player.spigot().sendMessage(
                    GeneralUtils.createTextComponent(
                        ChatColor.GOLD.toString() + "[ModersLib] Your plugins have updates. ${ChatColor.GOLD}Click ${ChatColor.BLUE}here ${ChatColor.GOLD}to view!",
                        ChatColor.GOLD.toString() + "[ModersLib] View updatable plugins.",
                        "/moderslib viewupdates"
                    )
                )
    }
}
