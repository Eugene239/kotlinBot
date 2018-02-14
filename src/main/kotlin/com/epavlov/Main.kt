package com.epavlov

import com.epavlov.bot.BotImpl
import com.epavlov.bot.BotImpl.safe
import com.epavlov.shedule.ScheduleChecker
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import java.util.*
import kotlin.concurrent.schedule


fun main(args: Array<String>) {
    ApiContextInitializer.init()
    safe {
        val telegramBotsApi = TelegramBotsApi()
        telegramBotsApi.registerBot(BotImpl)
        Timer("checkTracks", false)
                .schedule(3000, 60 * 60000) {
                    ScheduleChecker.checkTracks()
                }
    }

}


