package com.strizhonovapps.anylangapp.viewsupport

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType
import com.strizhonovapps.anylangapp.view.DEFAULT_LANG_KEY
import java.util.*

abstract class BaseLanguageDialogFactory(private val wordService: WordService,
                                         private val context: AppCompatActivity,
                                         private val setLangFunction: (LanguageType, String) -> Unit,
                                         private val finishActivityFunc: () -> Unit)
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
                    setLangFunction(type, DEFAULT_LANG_KEY)
                    finishActivityFunc()
                }
    }

}