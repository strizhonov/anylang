package com.strizhonovapps.anylangapp.service

import java.util.*

class WordStatusChecker {

    fun isWordIsActive(modificationDate: Date, targetDate: Date) =
            Date().time > modificationDate.time + WORD_FREEZE_TIME_MS
                    && Date().time > targetDate.time

}