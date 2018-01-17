package com.epavlov.dao

import com.epavlov.entity.Track
import com.epavlov.repository.TrackRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object TrackDAO {

    suspend fun get(id: String): Track? {
        return TrackRepository.getTrack(id)
    }

    fun getAsync(id: String): Deferred<Track?> {
        return async {
            return@async TrackRepository.getTrack(id)
        }
    }
}