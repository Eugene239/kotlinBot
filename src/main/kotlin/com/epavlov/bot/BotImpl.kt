package com.epavlov.bot

import com.epavlov.PropReader
import com.epavlov.entity.UserBot
import com.epavlov.repository.UserRepository
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class BotImpl : TelegramLongPollingBot() {
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
        p0?.message?.text?.let {
            log.debug("[MESSAGE] ${p0.message.from.id}: ${p0.message.text}")
            parseTextMessage(p0.message.from.id.toLong(), p0.message.text)
        }
        p0?.message?.sticker?.let {
            log.debug("[STICKER] ${p0.message.from.id}: ${p0.message.sticker.emoji} ${p0.message.sticker.fileId}")
        }
        p0?.callbackQuery?.let {
            log.debug("[CALLBACK] ${p0.callbackQuery?.from}: ${p0.callbackQuery?.data}")
        }

        //sticker.setSticker()
        //sendSticker()

    }

    fun parseTextMessage(userId: Long, message: String) {
        async {
            log.debug("asyncTask threadsCount: ${Thread.activeCount()}")
            val user:UserBot? = UserRepository.getUser(userId)
            user?.let {
                //checking is it command, then parse command
                if (isCommand(user, message)) return@async
            }
        }
    }

    fun SendStickertoUser(id: Int, s: String) {
        val sticker = SendSticker()
        sticker.setChatId(id.toLong())
        sticker.sticker = s
        sendSticker(sticker)
    }

    private fun isCommand(user:UserBot, msg:String): Boolean{
        var out = true
        when(msg.toLowerCase().trim()){
            "/mylist"-> {
                sendMessage(StringWrapper.wrapUserTrackList(user))
            }
            else->{
                out=false
            }
        }
        return out
    }

}