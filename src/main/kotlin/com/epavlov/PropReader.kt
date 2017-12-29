package com.epavlov
import java.util.*

object PropReader {
    private val prop = Properties()
    init{
        prop.load(ClassLoader.getSystemResourceAsStream("application.properties"))
    }
    fun getProperty(name:String): Any? {
        return prop[name]
    }
}