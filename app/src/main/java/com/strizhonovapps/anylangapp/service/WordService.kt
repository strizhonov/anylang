package com.strizhonovapps.anylangapp.service

import com.strizhonovapps.anylangapp.model.Word
import com.strizhonovapps.anylangapp.types.LanguageType
import java.io.InputStream

interface WordService {
    fun add(word: Word): Long
    fun get(id: Long): Word?
    fun update(word: Word): Int
    fun delete(id: Long): Int
    fun findAll(): List<Word>
    fun erase(): Int
    fun count(): Long
    fun getCurrent(): Word?
    fun processKnown(current: Word)
    fun processUnknown(current: Word)
    fun resetClosestWordsDates(count: Long)
    fun removeDuplicates()
    fun saveAllFromStream(inputStream: InputStream, separator: String)
    fun trimAndLowerCaseAllWords()
    fun resetProgress()
    fun syncAllTranslation()
    fun getAllTranslations(queryString: String): List<String>
    fun getSupportedLanguages(type: LanguageType): Set<String>
    fun setNativeLang(nativeLang: String)
    fun setStudyLang(studyLang: String)
    fun getCountOfActiveWords(): Long
}
