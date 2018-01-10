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
    suspend fun getUser(userId:Long): UserBot?{
         return suspendCoroutine { continuation ->
             run {
                 Repository.db.getReference("${UserBot.PATH}/$userId").addListenerForSingleValueEvent(object : ValueEventListener {
                     override fun onCancelled(p0: DatabaseError?) {
                         log.error(p0?.message,p0?.toException())
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
    suspend fun getList():List<UserBot>{
        return suspendCoroutine { continuation ->
            run{
                val list : ArrayList<UserBot> = ArrayList()
                Repository.db.getReference(UserBot.PATH).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        log.error(p0?.message,p0?.toException())
                        continuation.resume(list)
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                         p0?.children?.forEach { it->
                             try{
                                 val user:UserBot = it.getValue(UserBot::class.java)
                                 list.add(user)
                             }catch (e:Exception){
                                 log.error(e.message,e)
                             }
                         }
                        continuation.resume(list)
                    }

                })

            }
        }
    }

}