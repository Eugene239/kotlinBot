package com.epavlov.shedule

import com.epavlov.dao.TrackDAO
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

object ScheduleChecker {

     fun checkTracks() {
         launch {
             TrackDAO.getList().filter { it.users.size > 0 && it.notFound < 10 }
                     .forEach { TrackDAO.checkTrack(it) }
         }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            ScheduleChecker.checkTracks()
        }
    }
}