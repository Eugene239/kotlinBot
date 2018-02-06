package com.epavlov.parsers

import com.epavlov.entity.Track
import kotlinx.coroutines.experimental.Deferred

interface Parser {
    suspend fun getTrack(trackId:String): Track?
    fun getName():String
    fun getCode():Int
    suspend fun getTrackAsync(trackId: String):Deferred<Track?>
}