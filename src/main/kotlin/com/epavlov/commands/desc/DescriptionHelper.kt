package com.epavlov.commands.desc

import com.epavlov.PropReader
import com.epavlov.bot.BotImpl
import com.epavlov.dao.UserDAO
import kotlinx.coroutines.experimental.launch
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.methods.send.SendMessage

object DescriptionHelper {
    private val log = LogManager.getLogger(DescriptionHelper::class.java)

    private val userIdTrackIdmap = HashMap<Long, String>()


    fun isDesc(userId: Long): Boolean = userIdTrackIdmap.containsKey(userId)

    fun addToMap(userId: Long, trackId: String) {
        log.debug("[addToMap] <$userId,$trackId>")
        userIdTrackIdmap[userId] = trackId
        BotImpl.sendMessageToUser(SendMessage(userId,PropReader.getProperty("INPUT_DESC")))
    }

    fun saveDesc(userId: Long, text: String) {
        log.debug("[saveDesc] $userId: $text")
        if (isDesc(userId)) {
            val trackId = userIdTrackIdmap[userId]
            launch {
                userIdTrackIdmap.remove(userId)
                val user = UserDAO.get(userId)
                user?.trackList!![trackId]?.let {
                    it.name = text
                    UserDAO.save(user)
                    BotImpl.sendMessageToUser(SendMessage(userId,PropReader.getProperty("DESC_CHANGED")))
                }
            }
        } else {
            log.error("user don't have this description")
        }
    }

}