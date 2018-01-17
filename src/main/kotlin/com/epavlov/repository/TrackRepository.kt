package com.epavlov.repository

import com.epavlov.entity.Track
import com.epavlov.entity.UserBot
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.experimental.*
import org.apache.log4j.LogManager
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine


object TrackRepository {
    private val log = LogManager.getLogger(TrackRepository::class.java)

    /**
     * get Track from db
     */
    suspend fun getTrack(id: String): Track? {
        return suspendCoroutine {
            Repository.db.getReference("${Track.PATH}/$id").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    log.error(p0?.message, p0?.toException())
                    it.resume(null)
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    try {
                        val track: Track? = p0?.getValue(Track::class.java)
                        it.resume(track)
                    } catch (e: Exception) {
                        log.error(e.message, e)
                        it.resume(null)
                    }

                }
            })
        }
    }

    /**
     * get List of all tracks in DB
     */
    suspend fun getList(): List<Track> {
        val list: ArrayList<Track> = ArrayList()
        return suspendCoroutine {
            Repository.db.getReference(Track.PATH).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    log.error(p0?.message, p0?.toException())
                    it.resume(list)
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.children?.forEach { it ->
                        try {
                            val track: Track = it.getValue(Track::class.java)
                            list.add(track)
                        } catch (e: Exception) {
                            log.error(e.message, e)
                        }
                    }
                    it.resume(list)
                }
            })
        }
    }
}
