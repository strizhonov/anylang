package com.strizhonovapps.anylangapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.strizhonovapps.anylangapp.types.LanguageType
import com.strizhonovapps.anylangapp.view.MainActivity
import com.strizhonovapps.anylangapp.view.SelectLanguageDialog

private const val NATIVE_REQ_CODE = 109
private const val STUDY_REQ_CODE = 110

class OnAppStartActivity : Activity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = super.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        if (needToSetLanguages()) {
            startActivityToSetNativeLang()
        } else {
            startApp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            NATIVE_REQ_CODE -> {
                if (!preferences.contains(LanguageType.STUDY_LANGUAGE.toString())) {
                    startActivityToSetStudyLang()
                } else {
                    startApp()
                }
            }
            STUDY_REQ_CODE -> {
                markFirstRunAsFalse()
                startApp()
            }
        }
    }

    private fun startActivityToSetNativeLang() {
        val intent = Intent(this, SelectLanguageDialog::class.java)
        intent.putExtra(getString(R.string.init_extra_message_key), LanguageType.NATIVE_LANGUAGE.toString())
        startActivityForResult(intent, NATIVE_REQ_CODE)
    }

    private fun markFirstRunAsFalse() {
        val preferencesEditor = preferences.edit()
        preferencesEditor.putBoolean(getString(R.string.first_run_key), false)
        preferencesEditor.apply()
    }

    private fun startActivityToSetStudyLang() {
        val intent = Intent(this, SelectLanguageDialog::class.java)
        intent.putExtra(getString(R.string.init_extra_message_key), LanguageType.STUDY_LANGUAGE.toString())
        startActivityForResult(intent, STUDY_REQ_CODE)
    }

    private fun needToSetLanguages() = preferences.getBoolean(getString(R.string.first_run_key), true)
            && !preferences.contains(LanguageType.NATIVE_LANGUAGE.toString())

    private fun startApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}