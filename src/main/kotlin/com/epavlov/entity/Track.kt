package com.epavlov.entity

import java.util.*


class Track {

    var id: String? = null
    var status: String? = null
    var parserCode: Int = 0
    var text: String? = null
    var last_check: String? = null
    var time: String? = null
    var users = HashMap<String, Long>()

 /*   fun consume(track: Track) {
        text = track.text
        time = track.time
        parserCode = track.parserCode
        status = track.status
    }*/



    companion object {
        val PATH = Track::class.java.simpleName
    }

    override fun toString(): String {
        return "Track(id=$id, status=$status, parserCode=$parserCode, text=$text, last_check=$last_check, time=$time, users=$users)"
    }

}