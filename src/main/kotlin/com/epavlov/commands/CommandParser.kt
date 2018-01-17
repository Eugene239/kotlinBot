package com.epavlov.commands

import com.epavlov.dao.TrackDAO
import com.epavlov.dao.UserDAO
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.launch
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.objects.CallbackQuery

object CommandParser{
    private val log = LogManager.getLogger(CommandParser::class.java)
    private  val delimiter : String ="#"

    fun parseCommand(callback:CallbackQuery) {
        val command = callback.data.split(delimiter)[0]
        val value = callback.data.split(delimiter)[1]
        when(Command.valueOf(command)){
            Command.GET_TRACK -> {
                log.debug("$command $value")
                 getTrackCommand(callback.from.id.toLong(),value)
            }
        }
    }
    fun getTrackCommand(userId:Long, track:String){
        launch {
            StringWrapper.wrapUserTrack(UserDAO.getAsync(userId).await(),TrackDAO.getAsync(track).await())
        }
    }
}