package com.strizhonovapps.anylangapp.viewsupport

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.SHARED_PREFERENCES_FILE
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType

class NativeLanguageDialogFactory(private val wordService: WordService,
                                  private val context: AppCompatActivity)
    : BaseLanguageDialogFactory(wordService, context) {

    private var nativeLang: String? = null

    override fun getDialog(): AlertDialog {
        val dialogTemplateWithNegativeButton =
                super.getDialogTemplateWithDefaultNegativeButton(LanguageType.NATIVE_LANGUAGE)
        val availableLangs = super.getAvailableLangsNamesAndCodes(LanguageType.NATIVE_LANGUAGE)
        return updateAlertDialogWithLangSettings(dialogTemplateWithNegativeButton, availableLangs)
    }

    override fun setDefaultLanguage() {
        super.setDefaultLanguage(LanguageType.NATIVE_LANGUAGE)
        finishActivityWithEmptyResult()
    }

    private fun updateAlertDialogWithLangSettings(adBuilder: AlertDialog.Builder,
                                                  langs: Map<String, String>): AlertDialog {
        val items = langs.keys.toTypedArray()
        adBuilder.setSingleChoiceItems(items, 0) { _, n: Int ->
            nativeLang = langs[items[n]]!!
        }
        adBuilder.setTitle(context.getString(R.string.native_lang_dialog_title))
        adBuilder.setPositiveButton(context.getString(R.string.ok_content)) { dialog: DialogInterface, _ ->
            if (nativeLang == null) {
                nativeLang = langs[items[0]]!!
            }
            setNativeLanguageToPreferences()
            wordService.setNativeLang(nativeLang!!)
            dialog.dismiss()
            finishActivityWithEmptyResult()
        }
        return adBuilder.create()
    }

    private fun setNativeLanguageToPreferences() {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE,
                AppCompatActivity.MODE_PRIVATE)
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString(LanguageType.NATIVE_LANGUAGE.toString(), nativeLang)
        preferencesEditor.apply()
    }

}