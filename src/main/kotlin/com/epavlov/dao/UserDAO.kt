package com.epavlov.dao

import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.epavlov.entity.UserTrack
import com.epavlov.repository.UserRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager
import org.telegram.telegrambots.api.objects.User
import java.time.LocalDateTime

object UserDAO{

    private val log = LogManager.getLogger(UserDAO::class.java)
    /**
     * Check user, is he in db, if not add to db
     * change last message time in db to LocalDateTime.now()
     */
    fun checkUser(userTelegram: User){
        async {
            val userBot: UserBot? = get(userTelegram.id.toLong())
            if (userBot == null) {
                val newUser = UserBot(userTelegram)
                save(newUser)
                newUserCall(newUser)
            } else {
                userBot.lastMessageTime = LocalDateTime.now().toString()
                UserRepository.save(userBot)
            }
        }
    }

    /**
     * Calling this function if new user connecting to the bot
     */
    fun newUserCall(user:UserBot){
        log.info("[NEW USER]: $user")
    }

    /**
     * Get user  by ID
     */
    suspend fun get(id:Long):UserBot?{
        return  UserRepository.getUser(id)
    }

    /**
     * get deffered UserBot, to await async call
     */
    fun getAsync(id:Long): Deferred<UserBot?> {
        return async{
            return@async UserRepository.getUser(id)
        }
    }

    /**
     * delete track from user
     */
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

    /**
     * save track to user
     */
    fun saveTrack(id:Long,track: Track){

        async {
            val user = UserRepository.getUser(id)
            user?.let {
                if (!track.id.isNullOrEmpty()){
                    user.trackList.put(track.id!!, UserTrack(LocalDateTime.now().toString(),"",track.id))
                    save(user)

                    //checking is track have this user
                    if (!track.users.containsKey(user.id.toString())){
                        track.users[user.id.toString()] = user.id!!.toLong()
                        TrackDAO.save(track)
                    }
                }
            }
        }
    }

    /**
     * change track desc, to better understanding
     */
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

    /**
     * save new user to db
     */
    fun save(user:UserBot){
        log.info("[save] $user")
        UserRepository.save(user)
    }
    /**
     * check is user already got this track
     */
    suspend fun containTrack(userId: Long, trackId: String):Boolean{
         return  get(userId)?.trackList?.get(trackId)!=null

    }
}