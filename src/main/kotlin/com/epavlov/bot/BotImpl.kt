package com.epavlov.bot

import com.epavlov.PropReader
import com.epavlov.commands.CommandParser
import com.epavlov.dao.TrackDAO
import com.epavlov.dao.UserDAO
import com.epavlov.entity.UserBot
import com.epavlov.parsers.MainParser
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.util.*

/**
 * todo 18.01.2018
 *  -   add/delete info
 *  -   fix dbl loading
 */
object BotImpl : TelegramLongPollingBot() {
    private val log = LogManager.getLogger(BotImpl::class.java)
    private val cantUnderstand = PropReader.getProperty("cantUnderstand").split(",").toList()
    private val random = Random()

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

        p0?.message?.sticker?.let {
            log.debug("[STICKER] ${p0.message.from.id}: ${p0.message.sticker.emoji} ${p0.message.sticker.fileId}")
            sendStickertoUser(p0.message.chatId, PropReader.getProperty("onSticker.sticker"))
            sendMessageToUser(SendMessage(p0.message.chatId, PropReader.getProperty("onSticker.text")))
        }
        p0?.callbackQuery?.let {
            log.debug("[CALLBACK] ${p0.callbackQuery?.from}:  ${p0.callbackQuery.data}")
            CommandParser.parseCommand(p0.callbackQuery)
        }

        p0?.message?.text?.let {
            log.debug("[MESSAGE] ${p0.message.from.id}: ${p0.message.text}")
            parseTextMessage(p0.message.from.id.toLong(), p0.message)
        }
    }

    fun parseTextMessage(userId: Long, message: Message) {
        async {
            val user: UserBot? = UserDAO.get(userId)
            //checking is it command, then parse command
            if (isDefaultCommand(userId, user, message)) return@async
            if (MainParser.checkTrack(message.text)) {
                //user already had this track
                if (UserDAO.containTrack(userId, message.text)) {
                    log.debug("user already have this track $userId ${message.text}")
                    StringWrapper.wrapUserTrack(user, TrackDAO.get(message.text))
                    return@async
                }
                //this track contains in db, but user don't have it
                TrackDAO.get(message.text)?.let { track ->
                    log.debug("this track already stored in db ${track.id}")
                    UserDAO.saveTrack(userId, track)
                    StringWrapper.wrapUserTrack(user, track)
                    return@async
                }
                //finding track
                StringWrapper.sendTracksToUser(userId, MainParser.findTrack(userId, message.text))
            } else {
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
            "/help" ->{
                sendMessageToUser(SendMessage(id, "Сейчас идет проверка новой версии бота, если что-то идет не так, просто напишите об этом в чате)"))
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
            if (e.message?.toLowerCase()?.contains(PropReader.getProperty("FORBIDDEN").toLowerCase()) == true){
                user.isActive=false
                UserDAO.save(user)
            }
            log.error(user, e)
        }
    }

    fun sendMessageToUser(send: SendMessage) {
        safe {
            sendMessage(send)
        }
    }

    fun greeting(id: Long) {
        val sticker: String = PropReader.getProperty("greeting.sticker")
        if (sticker.isNotEmpty()) {
            sendStickertoUser(id, sticker)
        }
        sendMessageToUser(SendMessage(id, PropReader.getProperty("greeting")))
    }

    inline fun safe(noinline block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            LogManager.getLogger(block.javaClass.name).error(e.message, e)
        }
    }

}