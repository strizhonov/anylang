package com.strizhonovapps.anylangapp.translator

import com.strizhonovapps.anylangapp.types.LanguageType

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
    override fun getTranslation(queryWord: String): String? {
        val translations = getAllTranslations(queryWord)
        return if (translations.isEmpty()) {
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
    override fun getAllTranslations(queryWord: String): List<String> {
        return apiProcessor.getAllTranslations(queryWord, from, to)
    }

    override fun getSupportedLanguages(type: LanguageType): Set<String> {
        return apiProcessor.getAllLanguages(type)
    }

}