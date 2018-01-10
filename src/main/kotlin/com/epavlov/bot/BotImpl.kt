package com.epavlov.bot

import com.epavlov.PropReader
import com.epavlov.repository.UserRepository
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class BotImpl : TelegramLongPollingBot(){
    private val log = LogManager.getLogger(BotImpl::class.java)
    init{
        PropReader.getProperty("admins").toString()
                .split(",")
                .forEach { it-> sendMessage(SendMessage(it,"____ STARTED ____")) }
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
        //    SendStickertoUser(p0.message.from.id,story);
        }
        p0?.message?.sticker?.let {
            log.debug("[STICKER] ${p0.message.from.id}: ${p0.message.sticker.emoji} ${p0.message.sticker.fileId}")
        }

        //sticker.setSticker()
        //sendSticker()

    }

    fun parseTextMessage(userId:Long, message:String){
       async {
           val user= UserRepository.getUser(userId)
           sendMessage(SendMessage(userId,user?.fio))
       }
    }
    fun SendStickertoUser(id:Int, s:String){
        val sticker = SendSticker()
        sticker.setChatId(id.toLong())
        sticker.sticker = s
        sendSticker(sticker)
    }

}