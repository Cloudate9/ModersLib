package io.github.awesomemoder316.lib.api

import io.github.awesomemoder316.lib.utils.GeneralUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*

@Suppress("unused")
class ModersLibApi {

    /**
     * Github update check, using Github releases. Tags alone do not work.
     * This only works properly if the tag version is the same as the corresponding version's plugin.yml.
     * Users will not asked to update if the tag version contains "alpha" or "beta".
     *
     * @author awesomemoder316
     * @since June 30th, 2021
     *
     * @param githubUsername    The Github username the project is owned by.
     * @param githubProjectName The Github repository name of the project.
     * @param downloadURL       The url where the updated plugin can be fetched.
     * @param requesterPlugin   The plugin that calls this method.
     */
    fun githubUpdateCheck(githubUsername: String, githubProjectName: String, downloadURL: URL, requesterPlugin: Plugin) {

        object : BukkitRunnable() {
            override fun run() {

                val releasesURL = URL("https://api.github.com/repos/$githubUsername/$githubProjectName/releases")

                try {
                    val read = BufferedReader(
                        InputStreamReader(releasesURL.openStream())
                    )
                    var line: String?
                    var results = ""


                    while (read.readLine().also { line = it } != null) {
                        results += "\n$line"
                    }

                    read.close()

                    val jsonList = results.split(",")

                    var isLatestVersion = true //False if failed first check
                    var latestTag: String
                    val pages = ArrayList<String>()

                    for (part in jsonList) {

                        if (part.startsWith("\"tag_name\":\"")) {
                            latestTag = part
                                .removePrefix("\"tag_name\":\"")
                                .removeSuffix("\"")

                            if (latestTag == requesterPlugin.description.version) {

                                if (isLatestVersion) {

                                    if (!GeneralUtils.pastFirstCheck.contains(requesterPlugin)) {

                                        Bukkit.getConsoleSender().sendMessage(
                                            ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("upToDate")!!)
                                                .replace("\$requesterPlugin", requesterPlugin.name))

                                        GeneralUtils.pastFirstCheck.add(requesterPlugin)
                                    }

                                    repeatGithubUpdateCheck(
                                        githubUsername,
                                        githubProjectName,
                                        downloadURL,
                                        requesterPlugin
                                    )
                                    return
                                }

                                break
                            }

                            if (latestTag.contains("alpha", true) || latestTag.contains("beta", true)) continue

                            isLatestVersion = false

                            GeneralUtils.plugin.logger.info(
                                ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("updateAvailableMessage")!!)
                                    .replace("\$oldVersion", requesterPlugin.description.version)
                                    .replace("\$latestVersion", latestTag)
                                    .replace("\$pluginName", requesterPlugin.name)
                                    .replace("\$downloadURL", downloadURL.toString())
                            )

                            pages.add(
                                ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("updateAvailableMessage")!!)
                                    .replace("\$oldVersion", requesterPlugin.description.version)
                                    .replace("\$latestVersion", latestTag)
                                    .replace("\$pluginName", requesterPlugin.name)
                                    .replace("\$downloadURL", downloadURL.toString())
                            )
                        }
                    }

                    //Server has outdated version, reached end of update log

                    val book = ItemStack(Material.WRITTEN_BOOK)
                    val bookMeta = book.itemMeta as BookMeta

                    var authors = ""

                    for ((index, author) in requesterPlugin.description.authors.withIndex()) {
                        if (index != 0) authors += ", $author" else authors = author
                    }

                    bookMeta.author = authors
                    bookMeta.title =  "${requesterPlugin.description.name} update!"

                    bookMeta.pages = pages

                    book.itemMeta = bookMeta

                    GeneralUtils.updateDetected[requesterPlugin] = book



                } catch (e: IOException) {

                    Bukkit.getConsoleSender().sendMessage(
                        ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("updateCheckFail")!!)
                            .replace("\$requesterPlugin", requesterPlugin.name))

                    repeatGithubUpdateCheck(githubUsername, githubProjectName, downloadURL, requesterPlugin)
                }
            }
        }.runTaskAsynchronously(GeneralUtils.plugin)
    }

    /**
     *
     * This only works if your plugin is hosted on Spigot. If it is hosted somewhere else, use detailedUpdateCheck()
     *
     * @author awesomemoder316
     * @since June 30th, 2021
     *
     * @param resourceId            The resourceId of your plugin, which can be found on spigot. Only use this if your plugin is hosted on Spigot.
     * @param requesterPlugin       The plugin that calls this method.
     * @param downloadURL           The url where the updated plugin can be fetched.
     */
    fun spigotUpdateCheck(resourceId: Int, requesterPlugin: Plugin, downloadURL: URL) {
        if (GeneralUtils.plugin.config.getList("ignoreUpdates")?.contains(requesterPlugin.name) == false) {
            Bukkit.getScheduler().runTaskAsynchronously(GeneralUtils.plugin, Runnable {
                try {
                    val read = BufferedReader(
                        InputStreamReader(URL("https://api.spigotmc.org/legacy/update.php?resource=$resourceId").openStream())
                    )

                    val result = read.readLine() //Cause this site just has one line, which is the latest version.
                    read.close()

                    if (result != requesterPlugin.description.version) {

                        val book = ItemStack(Material.WRITTEN_BOOK)
                        val bookMeta = book.itemMeta as BookMeta

                        bookMeta.lore = listOf("No changelog available!")
                        bookMeta.pages = listOf(
                            ChatColor.translateAlternateColorCodes(
                                '&',
                                GeneralUtils.plugin.config.getString("updateAvailableMessage")!!
                            )
                                .replace("\$oldVersion", requesterPlugin.description.version)
                                .replace("\$latestVersion", result)
                                .replace("\$pluginName", requesterPlugin.name)
                                .replace("\$downloadURL", downloadURL.toString())
                        )

                        bookMeta.title = "${requesterPlugin.name} update"

                        var authors = ""

                        for ((index, author) in requesterPlugin.description.authors.withIndex()) {
                            if (index != 0) authors += ", $author" else authors = author
                        }

                        bookMeta.author = authors

                        book.itemMeta = bookMeta

                        GeneralUtils.updateDetected[requesterPlugin] = book

                        GeneralUtils.plugin.logger.info(
                            ChatColor.translateAlternateColorCodes(
                                '&',
                                GeneralUtils.plugin.config.getString("updateAvailableMessage")!!
                            )
                                .replace("\$oldVersion", requesterPlugin.description.version)
                                .replace("\$latestVersion", result)
                                .replace("\$pluginName", requesterPlugin.name)
                                .replace("\$downloadURL", downloadURL.toString())
                        )

                    } else {

                        if (!GeneralUtils.pastFirstCheck.contains(requesterPlugin)) {

                            Bukkit.getConsoleSender().sendMessage(
                                ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("upToDate")!!)
                                    .replace("\$requesterPlugin", requesterPlugin.name))

                            GeneralUtils.pastFirstCheck.add(requesterPlugin)
                        }

                        repeatSpigotUpdateCheck(resourceId, requesterPlugin, downloadURL)
                    }

                } catch (exception: IOException) {

                    Bukkit.getConsoleSender().sendMessage(
                        ChatColor.translateAlternateColorCodes('&', GeneralUtils.plugin.config.getString("updateCheckFail")!!)
                            .replace("\$requesterPlugin", requesterPlugin.name))

                    repeatSpigotUpdateCheck(resourceId, requesterPlugin, downloadURL)
                }
            })
        }
        //Do nothing if use has requested not to check for updates
    }

    fun getUpdateDetected(): HashMap<Plugin, ItemStack> {
        return GeneralUtils.updateDetected
    }

    fun getpastFirstCheck(): ArrayList<Plugin> {
        return GeneralUtils.pastFirstCheck
    }


    private fun repeatSpigotUpdateCheck(resourceId: Int, requesterPlugin: Plugin, downloadURL: URL) {
        object : BukkitRunnable() {
            override fun run() {
                spigotUpdateCheck(resourceId, requesterPlugin, downloadURL)
            }
        }.runTaskLaterAsynchronously(GeneralUtils.plugin, 576000) //Runs 8 hours later
    }

    private fun repeatGithubUpdateCheck(githubUsername: String, githubProjectName: String, downloadURL: URL, requesterPlugin: Plugin) {
        object: BukkitRunnable() {
            override fun run() {
                githubUpdateCheck(githubUsername, githubProjectName, downloadURL, requesterPlugin)
            }
        }.runTaskLaterAsynchronously(GeneralUtils.plugin, 576000) //Runs 8 hours later
    }

}