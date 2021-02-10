package com.strizhonovapps.anylangapp.service

import java.util.*

class TextPrettier {

    private val forbiddenCharacters: List<Char> = mutableListOf(8212.toChar(), '-')

    /**
     * Lowercase and trim (including some dash symbols) string
     */
    fun trimAndLowerCase(text: String?): String? {
        if (text == null) {
            return null
        }
        var length = text.length
        var start = 0
        while (start < length &&
                (Character.isWhitespace(text[start])
                        || forbiddenCharacters.contains(text[start]))) {
            start++
        }
        while (start < length &&
                (Character.isWhitespace(text[length - 1])
                        || forbiddenCharacters.contains(text[length - 1]))) {
            length--
        }
        return if (start > 0 || length < text.length) text.substring(start, length).toLowerCase(Locale.ROOT)
        else text.toLowerCase(Locale.ROOT)
    }

}