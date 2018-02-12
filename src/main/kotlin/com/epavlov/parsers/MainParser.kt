package com.epavlov.parsers

import com.epavlov.entity.Track
import com.epavlov.parsers.cainiao.CainiaoParser
import com.epavlov.parsers.pochtaru.ParserPochtaRu
import com.epavlov.parsers.track17.Parser17Track
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.apache.log4j.LogManager
import java.util.*
import java.util.regex.Pattern


object MainParser {
    private val log = LogManager.getLogger(MainParser::class.java)
    private val parserMap: HashMap<Int, Parser> = HashMap()
    private val pattern = Pattern.compile(".*[0-9]{5,}.*")

    init {
        parserMap[CainiaoParser.getCode()] = CainiaoParser
        parserMap[Parser17Track.getCode()] = Parser17Track
        parserMap[ParserPochtaRu.getCode()] = ParserPochtaRu
    }

    suspend fun parse(text: String): Track? {
        return null
    }

    suspend fun findTrack(userId: Long, text: String): List<Track?> {
        log.debug("findTrack userId: $userId text: $text parsers: ${parserMap.size}")
        val list = Collections.synchronizedList(ArrayList<Track?>())
        parserMap.values.forEach { it ->
            val deftrack = it.getTrackAsync(text)
            async {
                list.add(deftrack.await())
            }
        }
        while (list.size< parserMap.size){ }
        return list
    }

    fun getParser(parserCode: Int): String {
        return parserMap[parserCode]!!.getName()
    }

    fun checkTrack(text: String): Boolean {
        return pattern.matcher(text).matches()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val l  = MainParser.findTrack(172189604, "RF519862712SG")
            println("#####################")
            l.forEach({
               println(it.toString())
                println()
            })

            println("#####################")
        }
    }
}