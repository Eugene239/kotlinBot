package com.epavlov.wrapper

import com.epavlov.PropReader
import com.epavlov.bot.BotImpl
import com.epavlov.commands.Command
import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.epavlov.parsers.MainParser
import com.google.gson.Gson
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*

object StringWrapper {
    private val log = LogManager.getLogger(StringWrapper::class.java)
    private val gson = Gson()
    fun wrapUserTrackList(userBot: UserBot): SendMessage {
        val out = SendMessage()
        out.setChatId(userBot.id)
        if (userBot.trackList.isNotEmpty()) {
            val keyboardMarkup = InlineKeyboardMarkup()
            userBot.trackList.forEach({ id, track ->
                val list = ArrayList<InlineKeyboardButton>()
                val button = InlineKeyboardButton().setText(track.toKeyBoardText()).setCallbackData("${Command.GET_TRACK}#$id")
                list.add(button)
                keyboardMarkup.keyboard.add(list)
            })
            out.replyMarkup = keyboardMarkup
            out.text = PropReader.getProperty("yourTracks")
        } else {
            //user don't have tracks
            out.text = PropReader.getProperty("yourTracks.empty")
        }
        return out
    }

    fun wrapUserTrack(userBot: UserBot?, track: Track?) {
        userBot?.let {
            track?.let {
                log.debug("wrapUserTrack $userBot $track")
                val send = SendMessage(userBot.id, getTextMessage(userBot, track))
                send.replyMarkup = getTrackKeyboard(track.id)
                BotImpl.sendMessageToUser(userBot, send)
            }
        }
    }

    /**
     * get Text track to User
     */
    //todo add parser desc
    private fun getTextMessage(userBot: UserBot, track: Track): String {
        val name: String = userBot.trackList[track.id]?.name ?: ""
        if (!name.isEmpty()) {
            return "${track.id}\n\n" +
                    "${userBot.trackList[track.id]?.name}\n\n" +
                    "Статус: ${track.status ?: ""}\n\n" +
                    "${track.text ?: ""}\n\n" +
                    "Проверен: ${track.last_check}"
        }
        return "${track.id}\n\n" +
                "Статус: ${track.status ?: ""}\n\n" +
                "${track.text ?: ""}\n\n" +
                "Проверен: ${track.last_check}"
    }

    fun sendTrackId(userId: Long, trackId: String) {
        BotImpl.sendMessageToUser(SendMessage(userId, trackId))
    }

    private fun getTrackKeyboard(trackId: String?): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        trackId?.let {
            //get track id command
            val list = ArrayList<InlineKeyboardButton>()
            val getTrackId = InlineKeyboardButton().setText(PropReader.getProperty("getTrackId")).setCallbackData("${Command.GET_TRACK_ID}#$trackId")
            list.add(getTrackId)
            keyboardMarkup.keyboard.add(list)
        }
        return keyboardMarkup
    }

    fun sendTracksToUser(userId: Long, listTrack: List<Track?>) {
        val result = listTrack.filter { it != null }
        if (result.isEmpty()) {
            BotImpl.sendMessageToUser(SendMessage(userId, PropReader.getProperty("NOT_FOUND")))
            return
        }

        val keyboardMarkup = InlineKeyboardMarkup()
        result.forEach {
            it.let {
                val list = ArrayList<InlineKeyboardButton>()
                val btnData = "${Command.POST}#{\"id\"=\"${it?.id}\",\"parserCode\"=\"${it?.parserCode}\"}"
                log.debug(btnData)
                val btn = InlineKeyboardButton().setText(MainParser.getParser(it!!.parserCode)).setCallbackData(btnData)
                list.add(btn)
                keyboardMarkup.keyboard.add(list)
            }
        }
        BotImpl.sendMessageToUser(SendMessage(userId, PropReader.getProperty("CHOOSE_PARSER")).setReplyMarkup(keyboardMarkup))
    }
}