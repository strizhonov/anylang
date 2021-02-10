package com.strizhonovapps.anylangapp.translator

import android.net.Uri
import com.google.gson.Gson
import com.strizhonovapps.anylangapp.model.DictionaryResponse
import com.strizhonovapps.anylangapp.service.RequestSender
import com.strizhonovapps.anylangapp.types.LanguageType
import java.net.URL

private const val DICT_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?"
private const val DICT_LANG_LIST_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/getLangs?key="
private const val KEY = "key"
private const val LANGUAGE = "lang"
private const val QUERY_WORD = "text"

class YandexTranslationApiProcessor(private val dictKey: String,
                                    private val requestSender: RequestSender) {

    fun getAllTranslations(queryWord: String, from: String, to: String): List<String> {
        val translations = ArrayList<String>()
        val jsonResponse = getJsonWithTranslations(queryWord, from, to)
        val dictionaryResponse = Gson().fromJson(jsonResponse, DictionaryResponse::class.java)
        dictionaryResponse.def.forEach { it -> it.tr?.forEach { translations.add(it.text!!) } }
        return translations
    }

    fun getAllLanguages(type: LanguageType): Set<String> {
        val langs = HashSet<String>()
        val jsonArray = getJsonWithLangs()
        for (pair in Gson().fromJson(jsonArray, Array<String>::class.java)) {
            val pairArray = pair.split("-".toRegex(), 2).toTypedArray()
            when (type) {
                LanguageType.STUDY_LANGUAGE -> {
                    langs.add(pairArray[0])
                }
                LanguageType.NATIVE_LANGUAGE -> {
                    langs.add(pairArray[1])
                }
            }
        }
        return langs
    }

    /**
     * Get JSON from dictionary API
     *
     * @param queryWord word to translate
     * @param from      language to translate from
     * @param to        language to translate to
     * @return JSONObject with word translations
     */
    private fun getJsonWithTranslations(queryWord: String,
                                        from: String,
                                        to: String): String {
        val builtURI = Uri.parse(DICT_URL).buildUpon()
                .appendQueryParameter(KEY, dictKey)
                .appendQueryParameter(LANGUAGE, "$from-$to")
                .appendQueryParameter(QUERY_WORD, queryWord)
                .build()
        val requestURL = URL(builtURI.toString())
        return requestSender.sendGetRequestForJson(requestURL)
    }

    /**
     * Get Json list of  supported languages.
     *
     * @return json of supported languages in the form of ["ru-ru","ru-en","ru-pl"]
     */
    private fun getJsonWithLangs(): String {
        val builtURI = Uri.parse(DICT_LANG_LIST_URL)
                .buildUpon()
                .appendQueryParameter(KEY, dictKey)
                .build()
        val requestURL = URL(builtURI.toString())
        return requestSender.sendGetRequestForJson(requestURL)
    }

}