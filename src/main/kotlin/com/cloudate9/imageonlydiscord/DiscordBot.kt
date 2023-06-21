package com.cloudate9.imageonlydiscord

import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

suspend fun main() {
    val configFile = File("ImageOnlyDiscordConfig.json")
    val config: Config = if (configFile.exists()) {
        logger.info { "Config file found at ${configFile.absolutePath}" }
        Json.decodeFromString(configFile.readText())
    } else {
        logger.info { "No config file found, creating one now..." }
        var botToken: String? = null
        var channelRegex: String? = null
        var rejectionMessage: String? = null

        while (botToken == null) {
            logger.info { "Enter your Discord Bot Token" }
            botToken = readlnOrNull()
        }

        while (channelRegex == null) {
            logger.info { "Enter your Discord channel regex for image only restriction" }
            channelRegex = readlnOrNull()
        }

        while (rejectionMessage == null) {
            logger.info { "Enter your Discord rejection message when image is sent in non-image channel" }
            rejectionMessage = readlnOrNull()
        }

        Config(botToken, channelRegex, rejectionMessage).also {
            configFile.writeText(Json.encodeToString(it))
            logger.info { "Config file created at ${configFile.absolutePath}" }
        }

    }

    val kord = Kord(config.botToken)

    val channelRegex = Regex(config.channelRegex)
    kord.on<MessageCreateEvent> {
        if (member?.isOwner() == false || member?.isBot == true) return@on
        if ((message.getChannelOrNull() as TextChannel?)?.name?.let { channelRegex.matches(it) } == true) {
            if (this.message.attachments.isEmpty()) {
                message.delete("No picture detected in message in pics only chat")
                val rejectionMessageToUser = message.channel.createMessage {
                    content = "${message.author?.let { "<@${it.id}>, " }} ${config.rejectionMessage}"
                }
                "Deleted message from ${member?.displayName} in ${message.getGuildOrNull()?.name}".also(logger::info)
                delay(2000L)
                rejectionMessageToUser.delete("Auto delete rejection message")
            }
        }
    }

    logger.info { "Bot online" }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}