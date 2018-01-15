package com.epavlov
import java.util.*

object PropReader {
    private val prop = Properties()
    init{
        prop.load(ClassLoader.getSystemResourceAsStream("application.properties"))
        prop.load(ClassLoader.getSystemResourceAsStream("messages.properties"))
    }
    fun getProperty(name:String): Any? {
        return prop[name]
    }
}