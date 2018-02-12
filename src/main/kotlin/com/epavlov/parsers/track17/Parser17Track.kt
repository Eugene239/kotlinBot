package com.epavlov.parsers.track17

import com.epavlov.entity.Track
import com.epavlov.parsers.Parser
import com.epavlov.parsers.track17.entity.Track17
import com.epavlov.parsers.track17.entity.Track17FirstResponse
import com.google.gson.Gson
import com.squareup.okhttp.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import java.io.IOException
import kotlin.coroutines.experimental.suspendCoroutine

object Parser17Track : Parser {
    private val log = LogManager.getLogger(Parser17Track::class.java)
    private val gson = Gson()
    //todo add 17 track parse value
    override suspend fun getTrack(id: String): Track? {
        log.debug("getTrack $id")
        val json = createCall(id)
        log.debug(json)
        if (!json.isEmpty()) {
            val track17 = JSONParse(json, Track17::class.java)
            track17?.let {
                log.debug(track17.toString())
            }
        }
        return null
    }


    private suspend fun createCall(id: String): String {
        val client = OkHttpClient()
        val firstResponse = client.newCall(createRequest("", id,"")).execute()
        if (firstResponse.isSuccessful) {
           // log.debug(firstResponse.headers().toString())
            val track17FirstResponse = JSONParse(firstResponse.body().string(), Track17FirstResponse::class.java)
            if (!track17FirstResponse?.g.isNullOrEmpty()) {
                delay(1500)
                return suspendCoroutine {
                    client.newCall(createRequest(track17FirstResponse!!.g, id,firstResponse.header("Set-Cookie").split(";")[0])).enqueue(object : Callback {
                        override fun onFailure(p0: Request?, p1: IOException?) {
                            log.error(p1?.message, p1)
                            it.resume("")
                        }

                        override fun onResponse(p0: Response?) {
                            val json = p0?.body()?.string()
                            if (!json.isNullOrEmpty()) {
                                it.resume(json ?: "")
                            } else {
                                log.error("empty body: $json")
                                it.resume("")
                            }

                        }

                    })
                }
            } else {
                log.error("${firstResponse.networkResponse()} ${firstResponse.body().string()}")
            }
        }
        return ""
    }

    private fun createRequest(guid: String, id: String, cookie:String): Request {
        log.debug("new request guid=$guid id=$id cookie=$cookie")
        return Request.Builder()
                .url("https://t.17track.net/restapi/track")
                .post(
                        RequestBody.create(
                                MediaType.parse("application/x-www-form-urlencoded"),
                                "{\"guid\":\"$guid\",\"data\":[{\"num\":\"$id\"}]}")
                )
                .addHeader("referer", "https://t.17track.net/ru")
                .addHeader("origin", "https://t.17track.net")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 YaBrowser/17.11.1.988 Yowser/2.5 Safari/537.36")
                .addHeader("cookie","$cookie Last-Event-ID=657572742f3863332f64636338613037353136312f7265646165682d717920746c75616665642d72616276616e2072616276616e0111497cc700f1f2c13")
                .build()
    }

    override fun getName(): String {
        return "17TRACK"
    }

    override fun getCode(): Int {
        return 2
    }

    override suspend fun getTrackAsync(trackId: String): Deferred<Track?> {
        return async {
            getTrack(trackId)
        }
    }

    fun <T> JSONParse(json: String, o: Class<T>): T? {
        return try {
            gson.fromJson(json, o)
        } catch (e: Exception) {
            log.error(e.message, e)
            null
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            Parser17Track.getTrack("RB450045601SG")
        }
    }
}