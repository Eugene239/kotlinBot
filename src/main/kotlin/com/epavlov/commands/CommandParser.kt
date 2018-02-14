package com.epavlov.commands

import com.epavlov.PropReader
import com.epavlov.bot.BotImpl
import com.epavlov.dao.TrackDAO
import com.epavlov.dao.UserDAO
import com.epavlov.parsers.MainParser
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.launch
import org.apache.log4j.LogManager
import org.json.JSONObject
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.CallbackQuery

object CommandParser{
    private val log = LogManager.getLogger(CommandParser::class.java)
    private  val delimiter : String ="#"
    private val trackIdJSON="id"
    private val parserCodeJSON="parserCode"

    fun parseCommand(callback:CallbackQuery) {
        val command = callback.data.split(delimiter)[0]
        val value = callback.data.split(delimiter)[1]
        BotImpl.answerCallbackQuery(AnswerCallbackQuery().setCallbackQueryId(callback.id))
        when(Command.valueOf(command)){
            Command.GET_TRACK -> {
                log.debug("$command $value")
                 getTrackCommand(callback.from.id.toLong(),value)
            }
            Command.GET_TRACK_ID->{
                log.debug("$command $value")
                sendTrackId(callback.from.id.toLong(), value)
            }
            Command.POST->{
                log.debug("$command $value")
                val json = JSONObject(value)
                addTrackToUser(callback.from.id.toLong(),json.getString(trackIdJSON),json.getString(parserCodeJSON).toInt())
            }
            Command.DELETE->{
                log.debug("$command $value")
                UserDAO.deleteTrack(callback.from.id.toLong(),value)
                BotImpl.sendMessageToUser(SendMessage(callback.from.id.toLong(),PropReader.getProperty("TRACK_DELETED").replace("###",value)))
            }
        }
    }
    fun getTrackCommand(userId:Long, track:String){
        launch {
            StringWrapper.wrapUserTrack(UserDAO.getAsync(userId).await(),TrackDAO.getAsync(track).await())
        }
    }
    fun sendTrackId(userId:Long,trackId:String){
        launch {
            StringWrapper.sendTrackId(userId,trackId)
        }
    }
    fun addTrackToUser(userId: Long,trackId: String,parserCode:Int){
        log.info("[addTrackToUser]: userId=$userId trackId=$trackId parserCode=$parserCode")
        launch {
            if (!UserDAO.containTrack(userId,trackId)){
                val track = MainParser.findTrack(trackId,parserCode)
                log.debug("result=${MainParser.findTrack(trackId,parserCode)}")
                track?.let {
                    UserDAO.saveTrack(userId,track)
                    StringWrapper.wrapUserTrack(UserDAO.get(userId),track)
                    return@launch
                }
                BotImpl.sendMessageToUser(SendMessage(userId,PropReader.getProperty("NOT_FOUND")))
            }else{
                log.error("User already had this track")
                BotImpl.sendMessageToUser(SendMessage(userId,PropReader.getProperty("ALREADY_HAVE_PARSER")))
            }
        }

    }
}