package com.strizhonovapps.anylangapp.viewsupport

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.SHARED_PREFERENCES_FILE
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType

class StudyLanguageDialogFactory(private val wordService: WordService,
                                 private val context: AppCompatActivity)
    : BaseLanguageDialogFactory(wordService, context) {

    private var studyLang: String? = null

    override fun setDefaultLanguage() {
        super.setDefaultLanguage(LanguageType.STUDY_LANGUAGE)
        finishActivityWithEmptyResult()
    }

    override fun getDialog(): AlertDialog {
        val dialogTemplateWithNegativeButton =
                super.getDialogTemplateWithDefaultNegativeButton(LanguageType.STUDY_LANGUAGE)
        val availableLangs = super.getAvailableLangsNamesAndCodes(LanguageType.STUDY_LANGUAGE)
        return updateAlertDialogWithLangSettings(dialogTemplateWithNegativeButton, availableLangs)
    }

    private fun updateAlertDialogWithLangSettings(adBuilder: AlertDialog.Builder,
                                                  langs: Map<String, String>): AlertDialog {
        val items = langs.keys.toTypedArray()
        adBuilder.setSingleChoiceItems(items, 0) { _, n: Int ->
            studyLang = langs[items[n]]!!
        }
        adBuilder.setTitle(context.getString(R.string.study_lang_dialog_title))
        adBuilder.setPositiveButton(context.getString(R.string.ok_content)) { dialog: DialogInterface, _ ->
            if (studyLang == null) {
                studyLang = langs[items[0]]!!
            }
            setStudyLanguageToPreferences()
            wordService.setStudyLang(studyLang!!)
            dialog.dismiss()
            finishActivityWithEmptyResult()
        }
        return adBuilder.create()
    }

    private fun setStudyLanguageToPreferences() {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE,
                AppCompatActivity.MODE_PRIVATE)
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString(LanguageType.STUDY_LANGUAGE.toString(), studyLang)
        preferencesEditor.apply()
    }

}