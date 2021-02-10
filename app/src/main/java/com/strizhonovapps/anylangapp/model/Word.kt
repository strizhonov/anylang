package com.strizhonovapps.anylangapp.model

import java.util.*

data class Word(var id: Long? = null,
                var name: String? = null,
                var translation: String? = null,
                var lvl: Int? = null,
                var targetDate: Date = Date(),
                var modificationDate: Date = Date())
