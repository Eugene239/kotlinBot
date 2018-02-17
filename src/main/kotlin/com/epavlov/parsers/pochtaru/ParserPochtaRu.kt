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

/*
    todo add trackPochta 2 parser
 */
object ParserPochtaRu : Parser {
    private val log = LogManager.getLogger(ParserPochtaRu::class.java)
    private const val url="https://www.pochta.ru/tracking?p_p_id=trackingPortlet_WAR_portalportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=getList&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=2&barcodeList="

    override suspend fun getTrack(trackId: String): Track? {
        log.debug("[getTrack]: $trackId")
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
            val history = trackPochta?.list?.get(0)?.trackingItem?.trackingHistoryItemList?.get(0)
            history?.let {
                val track = Track()
                track.last_modify = LocalDateTime.now().toString()
                track.text = "${history.humanStatus}\n${history.description}"
                track.id = trackPochta.list[0].trackingItem.barcode
                track.status = trackPochta.list[0].trackingItem.globalStatus
                track.parserCode = 1
                track.time = history.date
                return track
            }
            log.error("not acceptable result: $response")
            return null
        } catch (e:Exception){
            log.error(e.message,e)
            return null
        }
    }

    private fun getTrack1Type(){

    }

    override fun getName(): String {
        return "Pochta.ru"
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
            println(getTrack("12161520001375"))
            println()
            println(getTrack("RA409014833FI"))
            println()
        }
    }
}