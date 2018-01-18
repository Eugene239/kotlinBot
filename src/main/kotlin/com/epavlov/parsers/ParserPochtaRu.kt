package com.epavlov.parsers

import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody

object ParserPochtaRu {
    @JvmStatic
    fun main(args: Array<String>) {
        val client = OkHttpClient()

        val request= Request.Builder()
                .url("https://www.pochta.ru/tracking?p_p_id=trackingPortlet_WAR_portalportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=getList&p_p_cacheability=cacheLevelPage&p_p_col_id=column-1&p_p_col_pos=1&p_p_col_count=2&barcodeList=ZA254184634HK")
//                .post(
//                        RequestBody.create(
//                                MediaType.parse("application/x-www-form-urlencoded"),
//                                "{\"guid\":\"\",\"data\":[{\"num\":\"RG719414992CN\"}]}")
//                )
                //  .addHeader("cookie","__cfduid=dcc4878378b3d34c0c8a6d0798f5e3dc71516024339; Last-Event-ID=657572742f3562332f37303131626639303136312f6461672d6c656e61702d717918211915cc881ac739a6")
               // .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
             //   .addHeader("origin","https://t.17track.net")
                .build()
        println(client.newCall(request).execute().body().string())
    }
}