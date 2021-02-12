package com.strizhonovapps.anylangapp.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.SHARED_PREFERENCES_FILE
import com.strizhonovapps.anylangapp.di.DaggerSelectLanguageDialogComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType
import java.util.*
import javax.inject.Inject

private const val DEFAULT_LANG_KEY = "en"

class SelectLanguageDialog() : AppCompatActivity() {

    private val availableLangs: SortedMap<String, String> = TreeMap()

    init {
        availableLangs["Belarusian"] = "be"
        availableLangs["Bulgarian"] = "bg"
        availableLangs["Czech"] = "cs"
        availableLangs["Danish"] = "da"
        availableLangs["German"] = "de"
        availableLangs["Greek"] = "el"
        availableLangs["English"] = "en"
        availableLangs["Spanish"] = "es"
        availableLangs["Estonian"] = "et"
        availableLangs["Finnish"] = "fi"
        availableLangs["French"] = "fr"
        availableLangs["Hungarian"] = "hu"
        availableLangs["Italian"] = "it"
        availableLangs["Lithuanian"] = "lt"
        availableLangs["Latvian"] = "lv"
        availableLangs["Mari"] = "mhr"
        availableLangs["Western Mari"] = "mrj"
        availableLangs["Dutch"] = "nl"
        availableLangs["Norwegian"] = "no"
        availableLangs["Polish"] = "pl"
        availableLangs["Portuguese"] = "pt"
        availableLangs["Russian"] = "ru"
        availableLangs["Slovak"] = "sk"
        availableLangs["Swedish"] = "sv"
        availableLangs["Turkish"] = "tr"
        availableLangs["Tatar"] = "tt"
        availableLangs["Ukrainian"] = "uk"
        availableLangs["Chinese"] = "zh"
    }

    private var nativeLang: String? = null
    private var studyLang: String? = null

    @Inject
    lateinit var wordService: WordService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerSelectLanguageDialogComponent.builder()
                .wordServiceModule(WordServiceModule(this))
                .build()
                .inject(this)

        showLangsSelectionDialog()
    }

    private fun showLangsSelectionDialog() {
        val langTypeToChange: String = intent.getStringExtra(super.getString(R.string.init_extra_message_key))!!
        val type = LanguageType.valueOf(langTypeToChange)
        try {
            val availableLangs = getAvailableLangsNamesAndCodes(type)
            createLangsDialog(availableLangs, type).show()
        } catch (e: Exception) {
            processExceptionOnSettingLangFromUser(e, type)
        }
    }

    private fun getAvailableLangsNamesAndCodes(type: LanguageType): Map<String, String> {
        val availableByApiLangs = wordService.getSupportedLanguages(type).toMutableSet()
        val availableByApiAndAppLangs = TreeMap<String, String>()
        for ((key, value) in availableLangs) {
            if (availableByApiLangs.contains(value)) {
                availableByApiAndAppLangs[key] = value
            }
        }
        return availableByApiAndAppLangs
    }

    private fun createLangsDialog(langs: Map<String, String>, type: LanguageType): AlertDialog {
        val adBuilder = AlertDialog.Builder(this)
        adBuilder.setNegativeButton(super.getString(R.string.cancel_content)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            setDefaultLanguages(type)
            finishActivityWithEmptyResult()
        }

        when (type) {
            LanguageType.NATIVE_LANGUAGE -> customizeBuilderForNativeLanguage(adBuilder, langs)
            LanguageType.STUDY_LANGUAGE -> customizeBuilderForStudyLanguage(adBuilder, langs)
        }
        return adBuilder.create()
    }

    private fun customizeBuilderForNativeLanguage(adBuilder: AlertDialog.Builder, langs: Map<String, String>) {
        val items = langs.keys.toTypedArray()
        adBuilder.setSingleChoiceItems(items, 0) { _: DialogInterface?, n: Int ->
            nativeLang = langs[items[n]]!!
        }
        adBuilder.setTitle(getString(R.string.native_lang_dialog_title))
        adBuilder.setPositiveButton(getString(R.string.ok_content)) { dialog: DialogInterface, _: Int ->
            if (nativeLang == null) {
                nativeLang = langs[items[0]]!!
            }
            setNativeLanguageToPreferences()
            wordService.setNativeLang(nativeLang!!)
            dialog.dismiss()
            finishActivityWithEmptyResult()
        }
    }

    private fun customizeBuilderForStudyLanguage(adBuilder: AlertDialog.Builder, langs: Map<String, String>) {
        val items = langs.keys.toTypedArray()
        adBuilder.setSingleChoiceItems(items, 0) { _: DialogInterface?, n: Int ->
            studyLang = langs[items[n]]!!
        }
        adBuilder.setTitle(getString(R.string.study_lang_dialog_title))
        adBuilder.setPositiveButton(getString(R.string.ok_content)) { dialog: DialogInterface, _: Int ->
            if (studyLang == null) {
                studyLang = langs[items[0]]!!
            }
            setStudyLanguageToPreferences()
            wordService.setStudyLang(studyLang!!)
            dialog.dismiss()
            finishActivityWithEmptyResult()
        }
    }

    private fun setStudyLanguageToPreferences() {
        val preferences: SharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString(LanguageType.STUDY_LANGUAGE.toString(), studyLang)
        preferencesEditor.apply()
    }

    private fun processExceptionOnSettingLangFromUser(e: Exception, type: LanguageType) {
        Log.e(this.javaClass.simpleName, "Unable to create dialog.", e)
        Toast.makeText(applicationContext,
                getString(R.string.no_internet_dialog_message),
                Toast.LENGTH_SHORT).show()
        setDefaultLanguages(type)
        finishActivityWithEmptyResult()
    }

    private fun setDefaultLanguages(type: LanguageType) {
        val preferences: SharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        when (type) {
            LanguageType.NATIVE_LANGUAGE -> {
                if (!preferences.contains(LanguageType.NATIVE_LANGUAGE.toString())) {
                    val preferencesEditor = preferences.edit()
                    preferencesEditor.putString(LanguageType.NATIVE_LANGUAGE.toString(), DEFAULT_LANG_KEY)
                    preferencesEditor.apply()
                }
            }
            LanguageType.STUDY_LANGUAGE -> {
                if (!preferences.contains(LanguageType.STUDY_LANGUAGE.toString())) {
                    val preferencesEditor = preferences.edit()
                    preferencesEditor.putString(LanguageType.STUDY_LANGUAGE.toString(), DEFAULT_LANG_KEY)
                    preferencesEditor.apply()
                }
            }
        }
    }

    private fun finishActivityWithEmptyResult() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun setNativeLanguageToPreferences() {
        val preferences: SharedPreferences = super.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE)
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString(LanguageType.NATIVE_LANGUAGE.toString(), nativeLang)
        preferencesEditor.apply()
    }

}