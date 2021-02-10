package com.strizhonovapps.anylangapp.di

import android.content.Context
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.SHARED_PREFERENCES_FILE
import com.strizhonovapps.anylangapp.dao.WordDao
import com.strizhonovapps.anylangapp.dao.WordDaoImpl
import com.strizhonovapps.anylangapp.service.*
import com.strizhonovapps.anylangapp.translator.TranslationProviderImpl
import com.strizhonovapps.anylangapp.translator.YandexTranslationApiProcessor
import com.strizhonovapps.anylangapp.types.LanguageType
import dagger.Module
import dagger.Provides
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Singleton

@Module
class WordServiceModule(private val context: Context) {

    private var instance: WordService? = null
    private val lock: Lock = ReentrantLock()

    @Singleton
    @Provides
    fun provideContext() = context

    @Singleton
    @Provides
    fun provideWordService(context: Context) = getWordService(context)

    private fun getWordService(context: Context): WordService {
        if (instance == null) {
            lock.lock()
            if (instance == null) {
                instance = create(context)
            }
            lock.unlock()
        }
        return instance!!
    }

    private fun create(context: Context): WordService {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        val from = preferences.getString(LanguageType.STUDY_LANGUAGE.toString(), context.getString(R.string.def_lang))
        val to = preferences.getString(LanguageType.NATIVE_LANGUAGE.toString(), context.getString(R.string.def_lang))
        val yandexApiKey = context.getString(R.string.yandex_api_key)
        val requestSender = RequestSender()
        val translationProvider = TranslationProviderImpl(from!!, to!!, YandexTranslationApiProcessor(yandexApiKey, requestSender))
        val wordDao: WordDao = WordDaoImpl(context)
        val wordStatusChecker = WordStatusChecker()
        val textPrettier = TextPrettier()
        val streamWordsRetriever = StreamWordsRetriever(textPrettier)
        return WordServiceImpl(wordDao, translationProvider, wordStatusChecker, streamWordsRetriever, textPrettier)
    }

}