package com.epavlov.repository

import com.epavlov.entity.UserBot
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.apache.log4j.LogManager
import kotlin.coroutines.experimental.suspendCoroutine

object UserRepository {
    private val log = LogManager.getLogger(UserRepository::class.java)
    /**
     * return user from db
     * return null if not found user
     */
    suspend fun getUser(userId: Long): UserBot? {
        log.debug("getUser: $userId")
        return suspendCoroutine {
            Repository.db.getReference("${UserBot.PATH}/$userId").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    log.error(p0?.message, p0?.toException())
                    it.resumeWithException(p0!!.toException())
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    log.debug("loaded: ${p0.toString()}")
                    try {
                        val user: UserBot? = p0?.getValue(UserBot::class.java)
                        it.resume(user)
                    } catch (e: Exception) {
                        log.error(e.message, e)
                        it.resume(null)
                    }
                }
            })
        }
    }

    suspend fun getList(): List<UserBot> {
        return suspendCoroutine {
            val list: ArrayList<UserBot> = ArrayList()
            Repository.db.getReference(UserBot.PATH).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    log.error(p0?.message, p0?.toException())
                    it.resume(list)
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    p0?.children?.forEach { it ->
                        try {
                            val user: UserBot = it.getValue(UserBot::class.java)
                            list.add(user)
                        } catch (e: Exception) {
                            log.error(e.message, e)
                        }
                    }
                    it.resume(list)
                }
            })
        }
    }

    fun save(user: UserBot) {
        Repository.db.getReference("${UserBot.PATH}/${user.id}").setValue(user)
    }
}