package com.epavlov.wrapper

import com.epavlov.PropReader
import com.epavlov.entity.UserBot
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*

object StringWrapper {


    fun wrapUserTrackList(userBot: UserBot): SendMessage {
        val out = SendMessage()
        out.text = PropReader.getProperty("yourTracks").toString()
        out.setChatId(userBot.id)
        val keyboardMarkup = InlineKeyboardMarkup()
        userBot.trackList.forEach({ id, track ->
            val list = ArrayList<InlineKeyboardButton>()
            val button = InlineKeyboardButton().setText(track.toKeyBoardText()).setCallbackData(id)
            list.add(button)
            keyboardMarkup.keyboard.add(list)
        })
        out.replyMarkup = keyboardMarkup
        return out
    }
}