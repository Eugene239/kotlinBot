package com.epavlov.bot

import com.epavlov.PropReader
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.methods.send.SendSticker
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class BotImpl : TelegramLongPollingBot(){
    //val hi :String="CAADAgAD1gIAAkKxQgMs-PMbiKMHAAEC"
    //val story:String="CAADAgAD0AIAAkKxQgPwqo_4vOxWIgI"
    init{
       // sendMessage(SendMessage(132718765,"ti cho ohuel?"))
//        val sticker=SendSticker()
//        sticker.setChatId(132718765)
//        sticker.sticker = "CAADAgADTAYAAqI03gbMJeqjHu5nyQI"
//        sendSticker(sticker)
     //   SendStickertoUser(132718765,hi)
    }
    override fun getBotToken(): String {
       return PropReader.getProperty("bot.token").toString()
    }

    override fun getBotUsername(): String {
        return PropReader.getProperty("bot.name").toString()
    }

    override fun onUpdateReceived(p0: Update?) {
        p0?.message?.text?.let {
            println("[MESSAGE] ${p0.message.from.id}: ${p0.message.text}")
        //    SendStickertoUser(p0.message.from.id,story);
        }
        p0?.message?.sticker?.let {
            println("[STICKER] ${p0.message.from.id}: ${p0.message.sticker.emoji} ${p0.message.sticker.fileId}")
        }

        //sticker.setSticker()
        //sendSticker()

    }


    fun SendStickertoUser(id:Int, s:String){
        val sticker = SendSticker()
        sticker.setChatId(id.toLong())
        sticker.sticker = s
        sendSticker(sticker)
    }

}