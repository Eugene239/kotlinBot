package com.epavlov.parsers

import com.epavlov.entity.Track

interface Parser {
    suspend fun getTrack(trackId:String): Track?
    fun getName():String
    fun getCode():Int
}