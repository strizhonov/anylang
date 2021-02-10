package com.strizhonovapps.anylangapp.translator

import com.strizhonovapps.anylangapp.types.LanguageType
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TranslationProviderImpl(
        private var from: String,
        private var to: String,
        private val apiProcessor: YandexTranslationApiProcessor)
    : TranslationProvider {

    override fun setNativeLanguage(language: String) {
        to = language
    }

    override fun setStudyLanguage(language: String) {
        from = language
    }

    /**
     * Get first translation from dictionary API
     *
     * @param queryWord word to translate
     * @return retrieved translation or null if there is no translation found
     */
    override fun getTranslation(queryWord: String) = submitCallable {
        val translations = getAllTranslations(queryWord)
        if (translations.isEmpty()) {
            null
        } else {
            translations[0]
        }
    }

    /**
     * Get translations from dictionary API
     *
     * @param queryWord word to translate
     * @return list of retrieved translations
     */
    override fun getAllTranslations(queryWord: String) = submitCallable { apiProcessor.getAllTranslations(queryWord, from, to) }

    override fun getSupportedLanguages(type: LanguageType) = submitCallable { apiProcessor.getAllLanguages(type) }

    private fun <R> submitCallable(func: () -> R) = Executors.newSingleThreadExecutor().submit(Callable {
        func.invoke()
    })[1000, TimeUnit.MILLISECONDS]!!

}