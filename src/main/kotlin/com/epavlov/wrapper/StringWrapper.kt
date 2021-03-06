package com.epavlov.wrapper

import com.epavlov.PropReader
import com.epavlov.bot.BotImpl
import com.epavlov.commands.Command
import com.epavlov.entity.ErrorMessage
import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.epavlov.parsers.MainParser
import com.epavlov.repository.ErrorMessageRepository
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton

object StringWrapper {
    private val log = LogManager.getLogger(StringWrapper::class.java)
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

    /**
     * todo refactor cuz if track deleted in i try to get it, i need to check if it has been deleted from db
     */
    fun wrapUserTrack(userBot: UserBot?, track: Track?) {
        userBot?.let {
            track?.let {
                log.debug("wrapUserTrack $userBot $track")
                val send = SendMessage(userBot.id, getTextMessage(userBot, track))
                send.replyMarkup = getTrackKeyboard(track.id)
                BotImpl.sendMessageToUser(userBot, send)
                return
            }
        }
        log.error("error input data $userBot $track")
        userBot?.let {

            //UserDAO.deleteTrack(userBot)
            BotImpl.sendMessageToUser(SendMessage(userBot.id, PropReader.getProperty("NOT_FOUND")))
        }

    }

    /**
     * get Text track to User
     */
    private fun getTextMessage(userBot: UserBot, track: Track): String {
        val name: String = userBot.trackList[track.id]?.name ?: ""
        if (!name.isEmpty()) {
            return "${track.id}\n\n" +
                    "${userBot.trackList[track.id]?.name}\n\n" +
                    "Статус: ${track.status ?: ""}\n\n" +
                    "${track.text ?: ""}\n\n" +
                    "Проверен: ${track.last_modify}\n" +
                    "Парсер ${MainParser.getParserName(track.parserCode)}"
        }
        return "${track.id}\n\n" +
                "Статус: ${track.status ?: ""}\n\n" +
                "${track.text ?: ""}\n\n" +
                "Проверен: ${track.last_modify}\n" +
                "Парсер ${MainParser.getParserName(track.parserCode)}"
    }

    fun sendTrackId(userId: Long, trackId: String) {
        BotImpl.sendMessageToUser(SendMessage(userId, trackId))
    }

    /**
     * todo modify dis
     */
    private fun getTrackKeyboard(trackId: String?): InlineKeyboardMarkup {
        val keyboardMarkup = InlineKeyboardMarkup()
        trackId?.let {
            //get track id command
            val list = ArrayList<InlineKeyboardButton>()
            val getTrackId = InlineKeyboardButton().setText(PropReader.getProperty("getTrackId")).setCallbackData("${Command.GET_TRACK_ID}#$trackId")
            list.add(getTrackId)

            val deleteList = ArrayList<InlineKeyboardButton>()
            val deleteButton = InlineKeyboardButton().setText(PropReader.getProperty("DELETE_TRACK")).setCallbackData("${Command.DELETE}#$trackId")
            deleteList.add(deleteButton)

            val descList = ArrayList<InlineKeyboardButton>()
            val descButton = InlineKeyboardButton().setText(PropReader.getProperty("CHANGE_DESC")).setCallbackData("${Command.ADD_DESC}#$trackId")
            descList.add(descButton)


            keyboardMarkup.keyboard.add(list)
            keyboardMarkup.keyboard.add(descList)
            keyboardMarkup.keyboard.add(deleteList)
        }
        return keyboardMarkup
    }

    /**
     * todo refactor, add track id, to log error
     */
    fun sendTracksToUser(userId: Long, listTrack: List<Track?>, trackId: String) {
        val result = listTrack.filter { it != null }
        log.info("[sendTracksToUser] got ${result.size} results")
        if (result.isEmpty()) {
            ErrorMessageRepository.save(ErrorMessage(userId, trackId))
            BotImpl.sendMessageToUser(SendMessage(userId, PropReader.getProperty("NOT_FOUND")))
            return
        }

        val keyboardMarkup = InlineKeyboardMarkup()
        result.forEach {
            it.let {
                val list = ArrayList<InlineKeyboardButton>()
                val btnData = "${Command.POST}#{\"id\":\"${it?.id}\",\"parserCode\":\"${it?.parserCode}\"}"
                // log.debug(btnData)
                val btn = InlineKeyboardButton().setText(MainParser.getParserName(it!!.parserCode)).setCallbackData(btnData)
                list.add(btn)
                keyboardMarkup.keyboard.add(list)
            }
        }
        BotImpl.sendMessageToUser(SendMessage(userId, PropReader.getProperty("CHOOSE_PARSER")).setReplyMarkup(keyboardMarkup))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            sendTracksToUser(189204145, MainParser.findTrack(189204145, "12161520001375"), "12161520001375")
        }
    }
}