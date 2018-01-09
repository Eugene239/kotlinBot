package com.epavlov.repository

import com.epavlov.entity.UserBot
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.experimental.suspendCoroutine

object UserRepository {

    /**
     * return user from db
     * return null if not found user
     */
    suspend fun getUser(userId:Long): UserBot?{
         return suspendCoroutine { continuation ->
             run {
                 Repository.db.getReference("${UserBot.PATH}/$userId").addListenerForSingleValueEvent(object : ValueEventListener {
                     override fun onCancelled(p0: DatabaseError?) {
                         continuation.resumeWithException(p0!!.toException())
                     }

                     override fun onDataChange(p0: DataSnapshot?) {
                         continuation.resume(p0!!.getValue(UserBot::class.java))
                         continuation.resume(null)
                     }
                 })
             }
         }
    }
}