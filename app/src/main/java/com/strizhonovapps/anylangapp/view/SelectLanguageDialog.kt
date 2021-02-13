package com.strizhonovapps.anylangapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerSelectLanguageDialogComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.LanguageType
import com.strizhonovapps.anylangapp.viewsupport.LanguageDialogFactory
import com.strizhonovapps.anylangapp.viewsupport.NativeLanguageDialogFactory
import com.strizhonovapps.anylangapp.viewsupport.StudyLanguageDialogFactory
import java.util.*
import javax.inject.Inject

class SelectLanguageDialog : AppCompatActivity() {

    private val typedLanguageDialogFactory =
            EnumMap<LanguageType, LanguageDialogFactory>(LanguageType::class.java)

    @Inject
    fun lateInject(wordService: WordService) {
        typedLanguageDialogFactory[LanguageType.NATIVE_LANGUAGE] =
                NativeLanguageDialogFactory(wordService, this)
        typedLanguageDialogFactory[LanguageType.STUDY_LANGUAGE] =
                StudyLanguageDialogFactory(wordService, this)
    }

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
            typedLanguageDialogFactory[type]?.getDialog()?.show()
        } catch (e: Exception) {
            processExceptionOnSettingLangFromUser(e, type)
        }
    }

    private fun processExceptionOnSettingLangFromUser(e: Exception, type: LanguageType) {
        Log.e(this.javaClass.simpleName, "Unable to create dialog.", e)
        Toast.makeText(applicationContext,
                getString(R.string.no_internet_dialog_message),
                Toast.LENGTH_SHORT).show()
        typedLanguageDialogFactory[type]?.setDefaultLanguage()
    }

}