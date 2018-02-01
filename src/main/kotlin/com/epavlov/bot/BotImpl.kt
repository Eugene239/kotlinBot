package com.epavlov.bot

import com.epavlov.PropReader
import com.epavlov.commands.CommandParser
import com.epavlov.dao.UserDAO
import com.epavlov.entity.UserBot
import com.epavlov.parsers.MainParser
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Send
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.util.*

/**
 * todo 18.01.2018
 *  -   add parsers
 *      /start
 *  -   add/delete info
 *  -   delete track
 *  -   getTrackID
 *  -   schedule
 *     /help
 */
object BotImpl : TelegramLongPollingBot() {
    private val log = LogManager.getLogger(BotImpl::class.java)
    private val cantUnderstand = PropReader.getProperty("cantUnderstand").split(",").toList()
    private val random  = Random()
    init {
        PropReader.getProperty("admins")
                .split(",")
                .forEach { it -> sendMessage(SendMessage(it, "____ STARTED ____")) }

    }

    override fun getBotToken(): String {
        return PropReader.getProperty("bot.token")
    }

    override fun getBotUsername(): String {
        return PropReader.getProperty("bot.name")
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
            sendStickertoUser(p0.message.chatId,PropReader.getProperty("onSticker.sticker"))
            sendMessageToUser(SendMessage(p0.message.chatId,PropReader.getProperty("onSticker.text")))
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
            if (isDefaultCommand(userId, user, message)) return@async
            if (MainParser.checkTrack(message.text)){
                MainParser.findTrack(userId, message.text)
            }else {
                //sendMessageToUser(SendMessage(userId,PropReader.getProperty("error.track.input")))
                sendStickertoUser(userId, cantUnderstand[random.nextInt(cantUnderstand.size)])
            }
        }
    }

    fun sendStickertoUser(id: Long?, s: String) {
        val sticker = SendSticker()
        sticker.setChatId(id)
        sticker.sticker = s
        sendSticker(sticker)
    }

    private fun isDefaultCommand(id: Long, user: UserBot?, msg: Message): Boolean {
        var out = true
        when (msg.text.toLowerCase().trim()) {
            "/mylist" -> {
                user?.let {
                    sendMessageToUser(user, StringWrapper.wrapUserTrackList(user))
                }
            }
            "/start" -> {
                   greeting(id)
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
            log.error(user, e)
        }
    }

    fun sendMessageToUser(send: SendMessage) {
        try {
            sendMessage(send)
        } catch (e: Exception) {
            log.error(send.chatId, e)
        }
    }

    fun greeting(id : Long){
        val sticker :String= PropReader.getProperty("greeting.sticker")
        if (sticker.isNotEmpty()){
            sendStickertoUser(id,sticker)
        }
        sendMessageToUser(SendMessage(id,PropReader.getProperty("greeting")))
    }

}