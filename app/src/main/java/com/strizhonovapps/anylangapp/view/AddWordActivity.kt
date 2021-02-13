package com.strizhonovapps.anylangapp.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerAddWordActivityComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.model.Word
import com.strizhonovapps.anylangapp.service.TwoSideIterator
import com.strizhonovapps.anylangapp.service.WordService
import javax.inject.Inject

class AddWordActivity : Activity(), View.OnClickListener {

    private var iterator: TwoSideIterator<String>? = null
    private lateinit var nameEditText: EditText
    private lateinit var transEditText: EditText

    @Inject
    lateinit var wordService: WordService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAddWordActivityComponent.builder()
                .wordServiceModule(WordServiceModule(this))
                .build()
                .inject(this)

        setContentView(R.layout.activity_add_word)
        title = getString(R.string.add_word_title)
        nameEditText = findViewById(R.id.name_edit_text)
        transEditText = findViewById(R.id.translation_edit_text)

        // Set listeners
        findViewById<Button>(R.id.auto_translate_button).setOnClickListener(this)
        findViewById<Button>(R.id.add_record_button).setOnClickListener(this)
        findViewById<Button>(R.id.back_auto_translate_button).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_record_button -> {
                val name = nameEditText.text.toString()
                val translation = transEditText.text.toString()
                wordService.add(Word(name = name, translation = translation))
                finish()
            }
            R.id.auto_translate_button -> {
                if (tryToInitIterator()) {
                    setNextTranslationToView()
                }
            }
            R.id.back_auto_translate_button -> {
                if (tryToInitIterator()) {
                    setPreviousTranslationToView()
                }
            }
        }
    }

    private fun tryToInitIterator() = try {
        val queryString = nameEditText.text.toString()
        if (iterator == null) {
            val toIterate = wordService.getAllTranslations(queryString)
            iterator = TwoSideIterator(toIterate)
        }
        true
    } catch (e: Exception) {
        Log.e(this.javaClass.simpleName, "Unable to find a translation", e)
        Toast.makeText(applicationContext,
                getString(R.string.unable_to_find_translation_message),
                Toast.LENGTH_SHORT).show()
        false
    }

    private fun setNextTranslationToView() {
        if (!iterator!!.hasPrevious()) {
            Toast.makeText(applicationContext,
                    getString(R.string.translation_not_found_message),
                    Toast.LENGTH_SHORT).show()
            return
        }
        transEditText.setText(iterator!!.next())
    }

    private fun setPreviousTranslationToView() {
        if (!iterator!!.hasPrevious()) {
            Toast.makeText(applicationContext,
                    getString(R.string.translation_not_found_message),
                    Toast.LENGTH_SHORT).show()
            return
        }
        transEditText.setText(iterator!!.previous())
    }

}