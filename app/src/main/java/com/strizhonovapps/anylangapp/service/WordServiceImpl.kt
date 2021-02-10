package com.strizhonovapps.anylangapp.service

import android.util.Log
import com.strizhonovapps.anylangapp.WORD_INITIAL_LEVEL
import com.strizhonovapps.anylangapp.dao.WordDao
import com.strizhonovapps.anylangapp.model.Word
import com.strizhonovapps.anylangapp.translator.TranslationProvider
import com.strizhonovapps.anylangapp.types.LanguageType
import java.io.InputStream
import java.util.*

const val WORD_FREEZE_TIME_MS = 3600000
private const val FREEZE_LENGTH_RATE = 1.5
private const val EMPTY_TRANSLATION = "/translation not found/"

class WordServiceImpl(private val wordDao: WordDao,
                      private val translationProvider: TranslationProvider,
                      private val wordStatusChecker: WordStatusChecker,
                      private val streamWordsRetriever: StreamWordsRetriever,
                      private val textPrettier: TextPrettier) : WordService {

    override fun add(word: Word) = wordDao.insert(word)
    override fun get(id: Long) = wordDao.get(id)
    override fun update(word: Word) = wordDao.update(word)
    override fun delete(id: Long) = wordDao.delete(id)
    override fun findAll() = wordDao.findAll()
    override fun erase() = wordDao.erase()
    override fun count() = wordDao.size()

    /**
     * Return word with closest target date and decent modification date
     *
     * @return word with closest target date and decent modification date
     */
    override fun getCurrent(): Word? {
        val sorted = wordDao.findAllSortedByTargetDate()
        for (current in sorted) {
            val now = Date().time
            if (now > current.modificationDate.time + WORD_FREEZE_TIME_MS
                    && now > current.targetDate.time) {
                return current
            }
        }
        return null
    }

    /**
     * Process known to user word by increasing its level and setting new dates
     *
     * @param current word to process
     */
    override fun processKnown(current: Word) {
        current.lvl = current.lvl!! + 1
        current.modificationDate = Date()
        val freezeTimeDays = current.lvl!! * FREEZE_LENGTH_RATE
        val freezeTimeMs = freezeTimeDays * 24 * 60 * 60 * 1000
        current.targetDate = Date(Date().time + freezeTimeMs.toLong())
        wordDao.update(current)
    }

    /**
     * Process known to user word by decreasing its level and setting new dates.
     * If current level is 0, decreasing is skipped
     *
     * @param current word to process
     */
    override fun processUnknown(current: Word) {
        if (current.lvl!! > 0) {
            current.lvl = current.lvl!! - 1
        }
        current.modificationDate = Date()
        val freezeTimeDays = current.lvl!! * FREEZE_LENGTH_RATE
        val freezeTimeMs = freezeTimeDays * 24 * 60 * 60 * 1000
        current.targetDate = Date(Date().time + freezeTimeMs.toLong())
        wordDao.update(current)
    }

    /**
     * Reset closest words by target date words's dates so that they are available for
     * learning mode
     *
     * @param count amount of words to reset
     */
    override fun resetClosestWordsDates(count: Long) {
        val sorted = wordDao.findAllSortedByTargetDate()
        val wordsToResetNumber = count.coerceAtMost(this.count())
        for (i in 0 until wordsToResetNumber) {
            val current = sorted[i.toInt()]
            current.targetDate = Date()
            current.modificationDate = Date(0)
            wordDao.update(current)
        }
    }

    /**
     * Remove duplications in database BY NAME so that
     * first name entry is untouchable, and all others are deleted
     */
    override fun removeDuplicates() {
        val words = wordDao.findAll().toMutableList()
        for (i in 0 until words.size - 1) {
            val currentName = words[i].name ?: continue
            var j = i + 1
            while (j < words.size) {
                val (id, name) = words[j]
                if (currentName == name) {
                    if (id == null) {
                        j++
                        continue
                    }
                    wordDao.delete(id)
                    words.removeAt(j)
                    j--
                }
                j++
            }
        }
    }

    /**
     * Transferring word and its translation from Input Stream.
     * Need to have exact SEPARATOR between the word and its translation.
     * Every word + translation need to be on the own line, otherwise
     * method execution correctness not guaranteed.
     */
    override fun saveAllFromStream(inputStream: InputStream, separator: String) =
            streamWordsRetriever.retrieve(inputStream, separator)
                    .forEach { wordDao.insert(it) }

    /**
     * Trim and lowercase all words' names and its translations.
     */
    override fun trimAndLowerCaseAllWords() {
        val words = wordDao.findAll()
        for (word in words) {
            word.name = textPrettier.trimAndLowerCase(word.name)
            word.translation = textPrettier.trimAndLowerCase(word.translation)
            wordDao.update(word)
        }
    }

    /**
     * Sets every word's level to INITIAL
     */
    override fun resetProgress() {
        val words = wordDao.findAll()
        for (word in words) {
            word.lvl = WORD_INITIAL_LEVEL
            wordDao.update(word)
        }
    }

    /**
     * Assign translation to every word from Yandex Dictionary API
     */
    override fun syncAllTranslation() {
        val words = wordDao.findAll()
        for (word in words) {
            word.translation = try {
                translationProvider.getTranslation(word.name ?: continue)
                        .also { textPrettier.trimAndLowerCase(it) }
            } catch (e: Exception) {
                Log.e(this.javaClass.simpleName, "Unable to get translation.", e)
                EMPTY_TRANSLATION
            }
            wordDao.update(word)
        }
    }

    override fun getAllTranslations(queryString: String) = translationProvider.getAllTranslations(queryString)
    override fun getSupportedLanguages(type: LanguageType) = translationProvider.getSupportedLanguages(type)
    override fun setNativeLang(nativeLang: String) = translationProvider.setNativeLanguage(nativeLang)
    override fun setStudyLang(studyLang: String) = translationProvider.setStudyLanguage(studyLang)

    override fun getCountOfActiveWords(): Long {
        var counter = 0
        val all = wordDao.findAll()
        for ((_, _, _, _, targetDate, modificationDate) in all) {
            if (wordStatusChecker.isWordIsActive(modificationDate, targetDate)) {
                counter++
            }
        }
        return counter.toLong()
    }

}
