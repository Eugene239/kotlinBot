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

    suspend fun getTrack(id: String): Track? {
        return suspendCoroutine { d ->
            run {
                Repository.db.getReference("${Track.PATH}/$id").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        log.error(p0?.message, p0?.toException())
                        d.resume(null)
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        try {
                            val track: Track? = p0?.getValue(Track::class.java)
                            d.resume(track)
                        } catch (e: Exception) {
                            log.error(e.message, e)
                            d.resume(null)
                        }

                    }
                })
            }
        }
    }

    suspend fun getList(): List<Track> {
        val list: ArrayList<Track> = ArrayList()
        return suspendCoroutine { d ->
            run {
                Repository.db.getReference(Track.PATH).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        log.error(p0?.message, p0?.toException())
                        d.resume(list)
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
                        d.resume(list)
                    }

                })
            }
        }
    }
}
