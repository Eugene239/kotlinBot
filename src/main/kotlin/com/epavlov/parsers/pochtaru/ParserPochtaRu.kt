package com.epavlov.parsers.pochtaru

import com.epavlov.entity.Track
import com.epavlov.parsers.Parser
import com.epavlov.parsers.pochtaru.entity.TrackPochta
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import org.json.JSONObject
import java.time.LocalDateTime

object ParserPochtaRu : Parser {
    private val log = LogManager.getLogger(ParserPochtaRu::class.java)
    private const val url="https://www.pochta.ru/tracking?p_p_id=trackingPortlet_WAR_portalportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=getList&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=2&barcodeList="

    override suspend fun getTrack(trackId: String): Track? {
        log.debug("getTrack: $trackId")
        val client = OkHttpClient()
        log.debug("$url$trackId")
        val request= Request.Builder()
                .url("$url$trackId")
                .build()
        val response = client.newCall(request).execute().body().string()
        log.debug(response)

        if (JSONObject(response).has("error")) {
            log.error("can't find track: $trackId")
            return null
        }
        try {
            val trackPochta = Gson().fromJson(response, TrackPochta::class.java)
            val history = trackPochta.list[0].trackingItem.trackingHistoryItemList[0]

            val track = Track()
            track.last_check = LocalDateTime.now().toString()
            track.last_modify = history.date
            track.text = "${history.humanStatus}\n${history.description}"
            track.id = trackPochta.list[0].trackingItem.barcode
            track.status = trackPochta.list[0].trackingItem.globalStatus
            track.parserCode = 1
            track.time = history.date
            return track
        } catch (e:Exception){
            log.error(e)
            return null
        }
    }

    override fun getName(): String {
        return "https://pochta.ru"
    }

    override fun getCode(): Int {
        return 1
    }

    override suspend fun getTrackAsync(trackId: String): Deferred<Track?> {
        return async {
            getTrack(trackId)
        }
    }


    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            println(getTrack("RM941563487CN"))
            println()
            println(getTrack("RG719414992CN"))
            println()
            println(getTrack("RA409014833FI"))
            println()
            println(getTrack("ZA277410909HK"))
            println()
            println(getTrack("RO577525516CN"))
            println()
            println(getTrack("06855536055"))
            println()
        }
    }
}