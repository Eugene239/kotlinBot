package com.epavlov.dao

import com.epavlov.entity.UserBot
import com.epavlov.repository.UserRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.objects.User
import java.time.LocalDateTime

object UserDAO{

    private val log = LogManager.getLogger(UserDAO::class.java)
    fun checkUser(userTelegram: User){
        async {
            val userBot: UserBot? = UserRepository.getUser(userTelegram.id.toLong())
            if (userBot == null) {
                val newUser = UserBot(userTelegram)
                UserRepository.save(newUser)
                newUserCall(newUser)
            } else {
                userBot.lastMessageTime = LocalDateTime.now().toString()
                UserRepository.save(userBot)
            }
        }
    }
    fun newUserCall(user:UserBot){
        log.info("[NEW USER]: $user")
    }

    suspend fun get(id:Long):UserBot?{
        return  UserRepository.getUser(id)
    }

    fun getAsync(id:Long): Deferred<UserBot?> {
        return async{
            return@async UserRepository.getUser(id)
        }
    }
}