package me.awesomemoder316.lib.command

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import dev.triumphteam.gui.guis.GuiItem
import me.awesomemoder316.lib.utils.GeneralUtils
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

internal class ViewUpdates: CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("rejectConsole")!!))
            return true
        }

        if (!sender.hasPermission("moderslib.receivePluginUpdates")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("noPermissionView")!!))
            return true
        }

        val updateGui = Gui.paginated()
            .title(Component.text("Plugins with updates"))
            .rows(6)
            .create()

        updateGui.setItem(
            6,
            3,
            ItemBuilder.from(Material.PAPER).name(Component.text(ChatColor.RED.toString() + "Previous"))
                .asGuiItem { updateGui.previous() })

        updateGui.setItem(
            6,
            7,
            ItemBuilder.from(Material.PAPER).name(Component.text(ChatColor.RED.toString() + "Next"))
                .asGuiItem { updateGui.next() })

        for (col in 1..9) {
            if (col != 3 && col != 7) {
                updateGui.setItem(6, col, GuiItem(Material.GRAY_STAINED_GLASS_PANE))
            }
        }

        for ((plugin, book) in GeneralUtils.updateDetected) {
            updateGui.addItem(ItemBuilder.from(Material.WRITTEN_BOOK)
                .name(Component.text(plugin.name))
                .lore(Component.text(ChatColor.YELLOW.toString() + "Click here to get update information for ${plugin.name}."))
                .asGuiItem { e: InventoryClickEvent -> (e.whoClicked as Player).openBook(book) })
        }

        updateGui.setDefaultClickAction { e -> e.isCancelled = true }
        updateGui.open(sender)

        return true

    }

}