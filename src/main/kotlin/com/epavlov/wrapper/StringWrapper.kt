package com.epavlov.wrapper

import com.epavlov.PropReader
import com.epavlov.bot.BotImpl
import com.epavlov.commands.Command
import com.epavlov.commands.CommandParser
import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.epavlov.repository.UserRepository
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*

object StringWrapper {
    private val log = LogManager.getLogger(StringWrapper::class.java)

    fun wrapUserTrackList(userBot: UserBot): SendMessage {
        val out = SendMessage()
        out.text = PropReader.getProperty("yourTracks").toString()
        out.setChatId(userBot.id)
        val keyboardMarkup = InlineKeyboardMarkup()
        userBot.trackList.forEach({ id, track ->
            val list = ArrayList<InlineKeyboardButton>()
            val button = InlineKeyboardButton().setText(track.toKeyBoardText()).setCallbackData("${Command.GET_TRACK}#$id")
            list.add(button)
            keyboardMarkup.keyboard.add(list)
        })
        out.replyMarkup = keyboardMarkup
        return out
    }

    fun wrapUserTrack(userBot: UserBot?, track: Track?) {
        userBot?.let {
            track?.let {
                log.debug("wrapUserTrack $userBot $track")
                val send = SendMessage(userBot.id, getTextMessage(userBot,track))
                BotImpl.sendMessageToUser(userBot, send)
            }
        }
    }

    /**
     * get Text track to User
     */
    private fun getTextMessage(userBot: UserBot, track: Track):String{
        val name : String=userBot.trackList[track.id]?.name?:""
        if (!name.isEmpty()){
            return "${track.id}\n\n" +
                    "${userBot.trackList[track.id]?.name}\n\n" +
                    "Статус: ${track.status ?: ""}\n\n" +
                    "${track.text ?: ""}\n\n" +
                    "Проверен: ${track.last_modify}"
        }
        return "${track.id}\n\n" +
                "Статус: ${track.status ?: ""}\n\n" +
                "${track.text ?: ""}\n\n" +
                "Проверен: ${track.last_modify}"
    }
}