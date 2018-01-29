package com.epavlov.dao

import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.epavlov.entity.UserTrack
import com.epavlov.repository.UserRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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

    fun deleteTrack(id:Long, trackId:String){
        async {
            val user= UserRepository.getUser(id)
            user?.let {
                if (user.trackList.containsKey(trackId)){
                    user.trackList.remove(trackId)
                    save(user)
                    TrackDAO.removeUser(trackId,id)
                }else {
                    log.error("${user.fio}  don't have track with id: $trackId")
                }
            }
        }
    }

    fun saveTrack(id:Long,track: Track){
        async {
            val user = UserRepository.getUser(id)
            user?.let {
                if (!track.id.isNullOrEmpty()){
                    user.trackList.put(track.id!!, UserTrack(LocalDateTime.now().toString(),"",track.id))
                    save(user)
                }
            }
        }
    }

    fun changeTrackDesc(id:Long,trackId:String,desc:String){
        async {
            val user=UserRepository.getUser(id)
            user?.let {
                val track= user.trackList[trackId]
                track?.let {
                    track.name=desc
                    user.trackList[trackId]=track
                    save(user)
                }
            }
        }
    }

    fun save(user:UserBot){
        UserRepository.save(user)
    }
}