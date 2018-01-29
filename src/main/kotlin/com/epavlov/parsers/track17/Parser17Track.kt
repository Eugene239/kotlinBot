package com.epavlov.parsers.track17

import com.epavlov.bot.BotImpl
import com.epavlov.entity.Track
import com.epavlov.parsers.Parser
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager

object Parser17Track : Parser {
    private val log = LogManager.getLogger(BotImpl::class.java)

    suspend override fun getTrack(id: String): Track? {
        log.debug("getTrack $id")
        log.debug(createCall(id))
       return null
    }

   private  fun createCall(id: String): String{
       val client = OkHttpClient()
       val request= Request.Builder()
               .url("https://t.17track.net/restapi/track")
               .post(
                       RequestBody.create(
                               MediaType.parse("application/x-www-form-urlencoded"),
                               "{\"guid\":\"\",\"data\":[{\"num\":\"$id\"}]}")
               )
               .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
               .addHeader("origin","https://t.17track.net")
               .build()

       val response= client.newCall(request).execute()
       if (response.isSuccessful){
           return  response.body().string()
       }else {
           log.error("${response.networkResponse().code()} ${response.body().string()}")
       }
       return ""
    }

    override fun getName(): String {
        return "17TRACK"
    }

    override fun getCode(): Int {
        return 0
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            Parser17Track.getTrack("RG719414992CN")
        }
    }
}