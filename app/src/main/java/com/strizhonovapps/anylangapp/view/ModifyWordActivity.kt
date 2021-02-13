package com.strizhonovapps.anylangapp.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerModifyWordActivityComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.model.Word
import com.strizhonovapps.anylangapp.service.TwoSideIterator
import com.strizhonovapps.anylangapp.service.WordService
import java.util.*
import javax.inject.Inject

class ModifyWordActivity : Activity(), View.OnClickListener {

    private lateinit var nameEditText: EditText
    private lateinit var transEditText: EditText
    private var iterator: TwoSideIterator<String>? = null

    @Inject
    lateinit var wordService: WordService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_modify_word)
        super.setTitle(getString(R.string.modify_word_activity_title))

        DaggerModifyWordActivityComponent.builder()
                .wordServiceModule(WordServiceModule(this))
                .build()
                .inject(this)

        initEditTexts()

        findViewById<Button>(R.id.auto_translate_button).setOnClickListener(this)
        findViewById<Button>(R.id.update_button).setOnClickListener(this)
        findViewById<Button>(R.id.delete_button).setOnClickListener(this)
        findViewById<Button>(R.id.back_auto_translate_button).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.update_button -> {
                updateWord()
                finish()
            }
            R.id.delete_button -> {
                deleteWord()
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

    private fun initEditTexts() {
        nameEditText = findViewById(R.id.name_edit_text)
        transEditText = findViewById(R.id.translation_edit_text)
        intent = super.getIntent()!!
        val name = intent.getStringExtra(getString(R.string.name_key))
        val trans = intent.getStringExtra(getString(R.string.translation_key))
        nameEditText.setText(name)
        transEditText.setText(trans)
    }

    private fun updateWord() {
        val id = intent!!.getLongExtra(super.getString(R.string.id_key), 0L)
        val lvl = intent!!.getIntExtra(super.getString(R.string.lvl_key), 0)
        val targetDateTimeKey: String = super.getString(R.string.target_date_time_key)
        val targetDate = Date(intent!!.getLongExtra(targetDateTimeKey, Date().time))
        val modificationDateTimeKey: String = super.getString(R.string.modification_date_time_key)
        val modificationDate = Date(intent!!.getLongExtra(modificationDateTimeKey, Date().time))

        val name = nameEditText.text.toString()
        val translation = transEditText.text.toString()

        val word = Word(
                id = id,
                name = name,
                translation = translation,
                lvl = lvl,
                targetDate = targetDate,
                modificationDate = modificationDate)
        wordService.update(word)
    }

    private fun deleteWord() {
        val id = intent!!.getLongExtra(super.getString(R.string.id_key), 0L)
        wordService.delete(id)
    }

    private fun tryToInitIterator(): Boolean {
        val queryString = nameEditText.text.toString()
        return try {
            if (iterator == null) {
                val toIterate = wordService.getAllTranslations(queryString)
                iterator = TwoSideIterator(toIterate)
            }
            true
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Unable to find a translation.", e)
            Toast.makeText(applicationContext,
                    getString(R.string.unable_to_find_translation_message),
                    Toast.LENGTH_SHORT).show()
            false
        }
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