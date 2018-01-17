package com.epavlov.bot

import com.epavlov.PropReader
import com.epavlov.commands.CommandParser
import com.epavlov.dao.UserDAO
import com.epavlov.entity.UserBot
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

object BotImpl : TelegramLongPollingBot() {
    private val log = LogManager.getLogger(BotImpl::class.java)

    init {
        PropReader.getProperty("admins").toString()
                .split(",")
                .forEach { it -> sendMessage(SendMessage(it, "____ STARTED ____")) }
    }

    override fun getBotToken(): String {
        return PropReader.getProperty("bot.token").toString()
    }

    override fun getBotUsername(): String {
        return PropReader.getProperty("bot.name").toString()
    }

    override fun onUpdateReceived(p0: Update?) {
        //checking user in db, if it doesn't contains it, save it
        p0?.message?.from?.let { UserDAO.checkUser(it) }

        p0?.message?.text?.let {
            log.debug("[MESSAGE] ${p0.message.from.id}: ${p0.message.text}")
            parseTextMessage(p0.message.from.id.toLong(), p0.message)
        }
        p0?.message?.sticker?.let {
            log.debug("[STICKER] ${p0.message.from.id}: ${p0.message.sticker.emoji} ${p0.message.sticker.fileId}")
        }
        p0?.callbackQuery?.let {
            log.debug("[CALLBACK] ${p0.callbackQuery?.from}:  ${p0.callbackQuery.data}")
            CommandParser.parseCommand(p0.callbackQuery)
        }
    }

    fun parseTextMessage(userId: Long, message: Message) {
        async {
            log.debug("asyncTask threadsCount: ${Thread.activeCount()}")
            val user: UserBot? = UserDAO.get(userId)
            //checking is it command, then parse command
            if (isDefaultCommand(user, message)) return@async

        }
    }

    fun SendStickertoUser(id: Int, s: String) {
        val sticker = SendSticker()
        sticker.setChatId(id.toLong())
        sticker.sticker = s
        sendSticker(sticker)
    }

    private fun isDefaultCommand(user: UserBot?, msg: Message): Boolean {
        var out = true
        when (msg.text.toLowerCase().trim()) {
            "/mylist" -> {
                user?.let {
                    sendMessageToUser(user, StringWrapper.wrapUserTrackList(user))
                }
            }
            "/start" -> {

            }
            else -> {
                out = false
            }
        }
        return out
    }

    fun sendMessageToUser(user: UserBot, send: SendMessage) {
        try {
            sendMessage(send)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

}