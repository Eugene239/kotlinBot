package com.epavlov.parsers.track17

import com.epavlov.bot.BotImpl
import com.epavlov.entity.Track
import com.epavlov.parsers.Parser
import com.epavlov.parsers.track17.entity.Track17
import com.google.gson.Gson
import com.squareup.okhttp.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import java.io.IOException
import kotlin.coroutines.experimental.suspendCoroutine

object Parser17Track : Parser {
    private val log = LogManager.getLogger(BotImpl::class.java)
    private val gson = Gson()

    override suspend fun getTrack(id: String): Track? {
        log.debug("getTrack $id")
        val json = createCall(id)
        log.debug(json)
        if (!json.isEmpty()) {
            try {
                val value = Gson().fromJson(json, Track17::class.java)
                log.debug(value.toString())
            } catch (e: Exception) {
                log.error(e.message, e)
                return null
            }
        }

        return null
    }
        //todo add creating 1 request to get guid, then second
    private suspend fun createCall(id: String): String {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://t.17track.net/restapi/track")
                .post(
                        RequestBody.create(
                                MediaType.parse("application/x-www-form-urlencoded"),
                                "{\"guid\":\"\",\"data\":[{\"num\":\"$id\"}]}")
                )
                .addHeader("referer", "https://t.17track.net/ru")
                .addHeader("origin", "https://t.17track.net")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 YaBrowser/17.11.1.988 Yowser/2.5 Safari/537.36")
                .build()


        client.newCall(request).execute()


        return suspendCoroutine {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(p0: Request?, p1: IOException?) {
                    log.error(p1?.message, p1)
                    it.resume("")
                }

                override fun onResponse(p0: Response?) {

                    val json = String(p0?.body()?.byteStream()?.readAllBytes() ?: byteArrayOf())
                    if (!json.isNullOrEmpty()) {
                        log.debug(json)
                        it.resume(json!!)
                    } else {
                        log.debug("empty body: $json")
                        it.resume("")
                    }

                }
            })
        }
    }
    private fun createRequest(){

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
            Parser17Track.getTrack("RB394094183SG")
        }
    }
}