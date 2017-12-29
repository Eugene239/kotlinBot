package com.epavlov

import com.epavlov.repository.Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.suspendCoroutine

class WrappedContinuation<T>(val c: Continuation<T>) : Continuation<T> {
    var isResolved = false
    override val context: CoroutineContext
        get() = c.context

    override fun resume(value: T) {
        if (!isResolved) {
            isResolved = true
            c.resume(value)
        }
    }

    override fun resumeWithException(exception: Throwable) {
        if (!isResolved) {
            isResolved = true
            c.resumeWithException(exception)
        }
    }

}

//inline suspend fun <T> suspendCoroutineW(crossinline block: (WrappedContinuation<T>) -> Unit): T =
//        suspendCoroutine { c ->
//            val wd = WrappedContinuation(c)
//            block(wd)
//        }

class TrackController {

     suspend fun getTrack(): String {
        return suspendCoroutine {d->
            async{
                Repository.db.getReference("Track").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        d.resumeWithException(p0!!.toException())

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        d.resume(p0.toString())

                    }
                })
                // delay(2000)
                //d.resume("ewdfewkf")
            }
        }
    }
    suspend fun getTrackCompletableDeffered(): String {
        val cd: CompletableDeferred<String> =CompletableDeferred<String>()
//        launch{
//            delay(2000);
//            cd.complete("kek")
//        }
        val reference =  Repository.db.getReference("Track")
        val listener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                cd.completeExceptionally(p0!!.toException())
            }
            override fun onDataChange(p0: DataSnapshot?) {
                cd.complete(p0.toString())
            }

        }
        reference.addListenerForSingleValueEvent(listener)
        val result= cd.await()
        reference.removeEventListener(listener)
        reference.onDisconnect()
        return result
    }


//    suspend fun getTrack2(): String {
//        var s: String = "";
//        launch {
//            s = getTrack()
//        }.join()
//        return s;
//    }
}
