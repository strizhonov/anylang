package com.strizhonovapps.anylangapp.service

import android.util.Log
import com.strizhonovapps.anylangapp.WORD_INITIAL_LEVEL
import com.strizhonovapps.anylangapp.model.Word
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class StreamWordsRetriever(private val textPrettier: TextPrettier) {

    fun retrieve(inputStream: InputStream, separator: String): List<Word> {
        val result = ArrayList<Word>()
        InputStreamReader(inputStream).use { inputStreamReader ->
            BufferedReader(inputStreamReader).use { bufferedReader ->
                var line: String
                while (bufferedReader.readLine().also { line = it } != null) {
                    val word = processLineForWord(line, separator)
                    if (word != null) {
                        result.add(word)
                    }
                }
            }
        }
        return result
    }

    private fun processLineForWord(line: String, separator: String): Word? {

        // If there is no match on separator, line will be skipped.
        if (!line.contains(separator)) {
            Log.w(this.javaClass.simpleName, "Illegal separator, skipping current line")
            return null
        }

        // If separator is found, method creates two string values:
        // a word name is before the separator, and a translation is after.
        // The separator is ignored.
        val i = line.indexOf(separator)
        val wordName = textPrettier.trimAndLowerCase(line.substring(0, i))
        val translation = textPrettier.trimAndLowerCase(line.substring(i + separator.length))
        return Word(
                name = wordName,
                translation = translation,
                lvl = WORD_INITIAL_LEVEL,
                targetDate = Date(),
                modificationDate = Date()
        )
    }

}