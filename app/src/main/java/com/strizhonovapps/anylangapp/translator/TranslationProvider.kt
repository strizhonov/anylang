package com.strizhonovapps.anylangapp.translator

import com.strizhonovapps.anylangapp.types.LanguageType

interface TranslationProvider {
    fun setNativeLanguage(language: String)
    fun setStudyLanguage(language: String)
    fun getTranslation(queryWord: String): String?
    fun getAllTranslations(queryWord: String): List<String>
    fun getSupportedLanguages(type: LanguageType): Set<String>
}
