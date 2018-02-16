package com.epavlov.shedule

import com.epavlov.dao.TrackDAO
import com.epavlov.dao.UserDAO
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

object ScheduleChecker {

    fun checkTracks() {
        launch {
            TrackDAO.getList().forEach {track->
                if (track.users.size > 0 && track.notFound < 10) {
                    TrackDAO.checkTrack(track)
                } else {
                    if (track.users.size == 0) {
                        TrackDAO.remove(track.id!!)
                        return@launch
                    }
                    if (track.notFound > 10) {
                        track.users.values.forEach { userId ->
                            UserDAO.deleteTrack(userId,track.id!!)
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            ScheduleChecker.checkTracks()
        }
    }
}