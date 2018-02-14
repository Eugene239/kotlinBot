package com.epavlov.dao

import com.epavlov.entity.Track
import com.epavlov.parsers.MainParser
import com.epavlov.repository.TrackRepository
import com.epavlov.wrapper.StringWrapper
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.apache.log4j.LogManager
import java.time.LocalDateTime

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

    fun removeUser(trackId: String, userId: Long) {
        log.debug("remove $trackId user=$userId")
        async {
            val track = TrackRepository.getTrack(trackId)
            track?.let {
                if (track.users.containsKey(userId.toString())) {
                    track.users.remove(userId.toString())
                    //check if track don't have users, remove it
                    if (track.users.size == 0)
                        remove(trackId)
                    else
                        save(track)
                } else {
                    log.error("track: $trackId don't have user with id: $userId")
                }
            }
        }
    }

    fun save(track: Track) {
        log.debug("[save] $track")
        TrackRepository.save(track)
    }

    fun remove(trackId: String) {
        log.debug("[remove] $trackId")
        TrackRepository.remove(trackId)
    }

    suspend fun getList(): List<Track> {
        return TrackRepository.getList()
    }

    suspend fun checkTrack(track: Track) {
            val newTrack= MainParser.findTrack(track.id!!, track.parserCode)
            delay(500)
            newTrack?.time?.let {
                if (!track.time.equals(newTrack.time)){
                    log.info("updated: \n$track \n$newTrack")

                    track.last_modify=LocalDateTime.now().toString()
                    track.status=newTrack.status
                    track.text=newTrack.text
                    track.time=newTrack.time
                    TrackDAO.save(track)

                    track.users.values.forEach{
                            log.info("notify user $it about $track")
                            StringWrapper.wrapUserTrack(UserDAO.get(it),track)
                    }
                }
            }
            if (newTrack==null){
                track.notFound++
                TrackDAO.save(track)
            }
    }
}