package com.epavlov.dao

import com.epavlov.entity.Track
import com.epavlov.repository.TrackRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.apache.log4j.LogManager

object TrackDAO {
    private val log = LogManager.getLogger(TrackDAO::class.java)

    suspend fun get(id: String): Track? {
        return TrackRepository.getTrack(id)
    }

    fun getAsync(id: String): Deferred<Track?> {
        return async {
            return@async TrackRepository.getTrack(id)
        }
    }
    fun removeUser(trackId:String, userId:Long){
        async {
            val track= TrackRepository.getTrack(trackId)
            track?.let {
                if (track.users.containsKey(userId.toString())){
                    track.users.remove(userId.toString())
                    TrackRepository.save(track)
                }else {
                    log.error("track: $trackId don't have user with id: $userId")
                }
            }
        }
    }
    fun save(track: Track){
        log.debug("[save] $track")
        TrackRepository.save(track)
    }
}