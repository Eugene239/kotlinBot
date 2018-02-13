package com.epavlov.parsers.cainiao

import com.epavlov.entity.Track
import com.epavlov.parsers.Parser
import com.google.gson.Gson
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime


object CainiaoParser : Parser {
    private val log = LogManager.getLogger(CainiaoParser::class.java)
    private val url = "https://global.cainiao.com/detail.htm?mailNoList="
    private val gson= Gson()
    override suspend fun getTrack(trackId: String): Track? {
        log.info("[getTrack]: $trackId")
        var out=""
        try {
            val connection = URL(url + trackId).openConnection()
            connection.connectTimeout = 30000
            val isR = connection.getInputStream()
            val br = BufferedReader(InputStreamReader(isR))

            br.readLines().forEach({
                if (it.contains("latestTrackingInfo") && it.contains("waybill_list_val_box")) {
                    out = it
                    return@forEach
                }
            })
        } catch (e: Exception) {
            log.error(e.message,e)
            return null
        }

        if (out == "") {
            log.error("can't find track $trackId")
            return null
        }

        // throw new TextException(TextException.WRONG_TRACK+": "+track_id);//  //не было данных
        try {
            val json = parseString(out)
            if (!json.isNullOrEmpty()) {
                val track = gson.fromJson(json, Track::class.java)
                track.last_check = LocalDateTime.now().toString()
                track.text=track.text?:""
                track.id = trackId
                return track
            }else return null
        } catch (e: Exception) {
            log.error(e.message,e)
            return null
        }
    }

    private fun parseString(out: String): String? {
        var out = out
        try {
            out = out.split("latestTrackingInfo".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            out = out.replace("&quot;".toRegex(), "")
            out = out.replace("</textarea>".toRegex(), "")
            out = "\"latestTrackingInfo" + out
            val str = out.split("\\{".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            out = str[1].split("}".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            out = out.replace("desc:", "{\"desc\":\"")
            out = out.replace(",timeZone:", "\"}")
            out = out.replace(",status:", "\",\"status\":\"")
            out = out.replace(",time:", "\",\"time\":\"")
            out = out.split("}".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "}"
            log.debug(out)
            return out
        } catch (e: Exception) {
            log.error(e.message,e)
            return null
            //   throw new TextException(TextException.WRONG_SITE_DATA);
        }

    }
    override fun getName(): String {
        return "Global Cainiao"
    }

    override fun getCode(): Int {
        return 0
    }

    override suspend fun getTrackAsync(trackId: String): Deferred<Track?> {
        return async {
            getTrack(trackId)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            println(getTrack("LM000175228CN"))
        }
    }
}