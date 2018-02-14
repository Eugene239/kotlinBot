package com.epavlov.dao

import com.epavlov.bot.BotImpl.safe
import com.epavlov.entity.ErrorMessage
import com.epavlov.repository.ErrorMessageRepository
import org.apache.log4j.LogManager

object ErrorDAO{
    private val log = LogManager.getLogger(ErrorDAO::class.java)

    fun save(userId:Long, text:String){
        log.info("[save] $userId $text")
        safe {
            ErrorMessageRepository.save(ErrorMessage(userId, text))
        }
    }
}