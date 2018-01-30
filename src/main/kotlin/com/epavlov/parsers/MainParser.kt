package com.epavlov.parsers

import com.epavlov.bot.BotImpl
import com.epavlov.dao.UserDAO
import com.epavlov.entity.Track
import com.epavlov.parsers.pochtaru.ParserPochtaRu
import com.epavlov.parsers.track17.Parser17Track
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage


object MainParser{
    private val log = LogManager.getLogger(MainParser::class.java)
    private val parserMap: HashMap<Int,Parser> = HashMap()

    init{
        parserMap[Parser17Track.getCode()]=Parser17Track
        parserMap[ParserPochtaRu.getCode()]=ParserPochtaRu
    }
    suspend fun parse(text:String):Track?{
        return null
    }

    fun findTrack(userId: Long, text: String){
        log.debug("findTrack userId: $userId text: $text parsers: ${parserMap.size}")
        runBlocking {
            parserMap.values.forEach { it->
                val track= it.getTrack(text)
                if(track!=null){
                    StringWrapper.wrapUserTrack(UserDAO.get(userId),track)
                }else{
                    BotImpl.sendMessage(SendMessage(userId,"Трек не найден: $text"))
                }
            }
        }
    }
    fun getParser(parserCode:Int):String{
        return  parserMap[parserCode]!!.getName()
    }
}