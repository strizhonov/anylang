package com.strizhonovapps.anylangapp.viewsupport

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.SHARED_PREFERENCES_FILE
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType
import java.util.*

private const val DEFAULT_LANG_KEY = "en"

abstract class BaseLanguageDialogFactory(private val wordService: WordService,
                                         private val context: AppCompatActivity)
    : LanguageDialogFactory {

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

    protected fun getAvailableLangsNamesAndCodes(type: LanguageType): Map<String, String> {
        val availableByApiLangs = wordService.getSupportedLanguages(type).toMutableSet()
        val availableByApiAndAppLangs = TreeMap<String, String>()
        for ((key, value) in availableLangs) {
            if (availableByApiLangs.contains(value)) {
                availableByApiAndAppLangs[key] = value
            }
        }
        return availableByApiAndAppLangs
    }

    protected fun getDialogTemplateWithDefaultNegativeButton(type: LanguageType): AlertDialog.Builder {
        return AlertDialog.Builder(context)
                .setNegativeButton(context.getString(R.string.cancel_content)) { dialog: DialogInterface, _ ->
                    dialog.dismiss()
                    setDefaultLanguage(type)
                    finishActivityWithEmptyResult()
                }
    }

    protected fun setDefaultLanguage(type: LanguageType) {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        if (!preferences.contains(type.toString())) {
            val preferencesEditor = preferences.edit()
            preferencesEditor.putString(type.toString(), DEFAULT_LANG_KEY)
            preferencesEditor.apply()
        }
    }

    protected fun finishActivityWithEmptyResult() {
        val returnIntent = Intent()
        context.setResult(Activity.RESULT_CANCELED, returnIntent)
        context.finish()
    }

}