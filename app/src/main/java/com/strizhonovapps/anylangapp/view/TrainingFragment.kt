package com.strizhonovapps.anylangapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerTrainingFragmentComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.model.Word
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.types.TrainingType
import java.util.*
import javax.inject.Inject

private const val NUMBER_OF_WORDS_TO_RESET = 10

class TrainingFragment : Fragment(), View.OnClickListener {

    private val trainingType = TrainingType.MIXED
    private var wordCard: LinearLayout? = null
    private var knownButton: LinearLayout? = null
    private var unknownButton: LinearLayout? = null
    private var topNavLayout: LinearLayout? = null
    private var editLayout: LinearLayout? = null
    private var deleteLayout: LinearLayout? = null
    private var wordEnTextView: TextView? = null
    private var wordRuTextView: TextView? = null
    private var wordLevelTxtView: TextView? = null
    private var wordsLeftTxtView: TextView? = null
    private var emptyTextView: TextView? = null
    private var showButton: Button? = null
    private var getWordsBtn: Button? = null
    private var current: Word? = null

    @Inject
    lateinit var wordService: WordService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        DaggerTrainingFragmentComponent.builder()
                .wordServiceModule(WordServiceModule(requireContext()))
                .build()
                .inject(this)
        val view: View = inflater.inflate(R.layout.fragment_training, container, false)
        initViewComponents(view)
        setListeners()
        refreshView()
        return view
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.remove_layout -> {
                wordService.delete(current!!.id!!)
                refreshView()
            }
            R.id.edit_layout -> {
                val modifyIntent = Intent(context, ModifyWordActivity::class.java)
                modifyIntent
                        .putExtra(super.getString(R.string.name_key), current?.name)
                        .putExtra(super.getString(R.string.translation_key), current?.translation)
                        .putExtra(super.getString(R.string.id_key), current?.id)
                        .putExtra(super.getString(R.string.target_date_time_key), current?.targetDate?.time)
                        .putExtra(super.getString(R.string.lvl_key), current?.lvl)
                        .putExtra(super.getString(R.string.modification_date_time_key), current?.modificationDate?.time)
                startActivity(modifyIntent)
            }
            R.id.show_button -> {
                setViewAfterShowButton()
            }
            R.id.known_button -> {
                wordService.processKnown(current!!)
                refreshView()
            }
            R.id.unknown_button -> {
                wordService.processUnknown(current!!)
                refreshView()
            }
            R.id.get_words_btn -> {
                wordService.resetClosestWordsDates(NUMBER_OF_WORDS_TO_RESET.toLong())
                refreshView()
            }
        }
    }

    private fun initViewComponents(view: View) {
        wordEnTextView = view.findViewById(R.id.word_en_text_view)
        wordRuTextView = view.findViewById(R.id.word_ru_text_view)
        emptyTextView = view.findViewById(R.id.when_empty_text_view)
        showButton = view.findViewById(R.id.show_button)
        knownButton = view.findViewById(R.id.known_button)
        unknownButton = view.findViewById(R.id.unknown_button)
        editLayout = view.findViewById(R.id.edit_layout)
        deleteLayout = view.findViewById(R.id.remove_layout)
        getWordsBtn = view.findViewById(R.id.get_words_btn)
        wordCard = view.findViewById(R.id.word_card)
        wordLevelTxtView = view.findViewById(R.id.word_level)
        wordsLeftTxtView = view.findViewById(R.id.words_left)
        topNavLayout = view.findViewById(R.id.training_top_nav)
    }

    private fun setListeners() {
        showButton!!.setOnClickListener(this)
        knownButton!!.setOnClickListener(this)
        unknownButton!!.setOnClickListener(this)
        editLayout!!.setOnClickListener(this)
        deleteLayout!!.setOnClickListener(this)
        getWordsBtn!!.setOnClickListener(this)
    }

    private fun refreshView() {
        current = wordService.getCurrent()
        if (current != null) {
            setWordView(current!!)
            return
        }

        if (wordService.count() > 0) {
            setNoActiveWordsView()
        } else {
            setNoWordsInStorageView()
        }
    }

    private fun setWordView(current: Word) {
        wordLevelTxtView!!.text = current.lvl.toString()
        val activeWords = wordService.getCountOfActiveWords()
        wordsLeftTxtView?.text = (activeWords - 1).toString()
        when (trainingType) {
            TrainingType.MIXED -> if (Random().nextBoolean()) {
                wordRuTextView!!.text = current.name
                wordEnTextView!!.text = current.translation
            } else {
                wordRuTextView!!.text = current.translation
                wordEnTextView!!.text = current.name
            }
            TrainingType.NATIVE_TO_STUDY -> {
                wordRuTextView!!.text = current.translation
                wordEnTextView!!.text = current.name
            }
            TrainingType.STUDY_TO_NATIVE -> {
                wordRuTextView!!.text = current.name
                wordEnTextView!!.text = current.translation
            }
        }
        setForPresentWordView()
    }

    private fun setForPresentWordView() {
        deleteLayout!!.visibility = View.VISIBLE
        wordRuTextView!!.visibility = View.VISIBLE
        knownButton!!.visibility = View.VISIBLE
        unknownButton!!.visibility = View.VISIBLE
        editLayout!!.visibility = View.VISIBLE
        showButton!!.visibility = View.VISIBLE
        wordEnTextView!!.visibility = View.VISIBLE
        wordCard!!.visibility = View.VISIBLE
        topNavLayout!!.visibility = View.VISIBLE
        wordRuTextView!!.visibility = View.INVISIBLE
        getWordsBtn!!.visibility = View.INVISIBLE
        emptyTextView!!.visibility = View.INVISIBLE
    }

    private fun setNoWordsInStorageView() {
        wordEnTextView!!.visibility = View.INVISIBLE
        wordRuTextView!!.visibility = View.INVISIBLE
        knownButton!!.visibility = View.INVISIBLE
        unknownButton!!.visibility = View.INVISIBLE
        showButton!!.visibility = View.INVISIBLE
        editLayout!!.visibility = View.INVISIBLE
        deleteLayout!!.visibility = View.INVISIBLE
        wordCard!!.visibility = View.INVISIBLE
        topNavLayout!!.visibility = View.INVISIBLE
        emptyTextView!!.visibility = View.VISIBLE
        emptyTextView?.setText(R.string.empty_list_text_message)
        getWordsBtn!!.visibility = View.GONE
    }

    private fun setNoActiveWordsView() {
        wordEnTextView!!.visibility = View.INVISIBLE
        wordRuTextView!!.visibility = View.INVISIBLE
        knownButton!!.visibility = View.INVISIBLE
        unknownButton!!.visibility = View.INVISIBLE
        showButton!!.visibility = View.INVISIBLE
        editLayout!!.visibility = View.INVISIBLE
        deleteLayout!!.visibility = View.INVISIBLE
        wordCard!!.visibility = View.INVISIBLE
        topNavLayout!!.visibility = View.INVISIBLE
        emptyTextView!!.visibility = View.VISIBLE
        emptyTextView?.setText(R.string.words_are_over_message)
        getWordsBtn!!.visibility = View.VISIBLE
    }

    private fun setViewAfterShowButton() {
        wordRuTextView!!.visibility = View.VISIBLE
        showButton!!.visibility = View.GONE
    }

}