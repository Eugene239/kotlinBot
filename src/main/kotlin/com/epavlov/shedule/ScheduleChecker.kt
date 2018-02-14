package com.epavlov.shedule

import com.epavlov.dao.TrackDAO
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

object ScheduleChecker {

    fun checkTracks() {
        launch {
            TrackDAO.getList().forEach {
                if (it.users.size > 0 && it.notFound < 10) {
                    TrackDAO.checkTrack(it)
                } else {
                    TrackDAO.remove(it.id!!)
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