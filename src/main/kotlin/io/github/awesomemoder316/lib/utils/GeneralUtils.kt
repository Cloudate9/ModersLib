package io.github.awesomemoder316.lib.utils

import io.github.awesomemoder316.lib.ModersLib
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.ArrayList
import java.util.HashMap

object GeneralUtils {
    val pastFirstCheck = ArrayList<Plugin>() //This is so that "no update" message does not get sent every 8 hours.
    lateinit var plugin: ModersLib
    var pre116 = true
    val updateDetected = HashMap<Plugin, ItemStack>()

    @Suppress("DEPRECATION")
    fun createTextComponent(message: String, hoverMessage: String?, clickCommand: String): TextComponent {
        val textComponent = TextComponent(message)

        if (hoverMessage != null) {

            textComponent.hoverEvent =
                if (pre116) {
                    HoverEvent( //For backwards compatibility, deprecated
                        HoverEvent.Action.SHOW_TEXT,
                        arrayOf(TextComponent(hoverMessage)) //For backwards compatibility.
                    )
                } else {
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text(hoverMessage)
                    )
                }
        }

        textComponent.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand)
        return textComponent
    }
}
