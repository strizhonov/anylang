package com.strizhonovapps.anylangapp.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerWordListFragmentComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.viewsupport.WordToListViewAdapter
import javax.inject.Inject

class WordListFragment : Fragment() {

    private var listView: ListView? = null
    private var wordCounterTextView: TextView? = null
    private var search: EditText? = null
    private var adapter: WordToListViewAdapter? = null

    @Inject
    lateinit var wordService: WordService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        DaggerWordListFragmentComponent.builder()
                .wordServiceModule(WordServiceModule(requireContext()))
                .build()
                .inject(this)

        val view: View = inflater.inflate(R.layout.fragment_word_list, container, false)
        wordCounterTextView = view.findViewById(R.id.word_counter_text_view)

        initListView(view)
        initSearch(view)
        initFloatingButtons(view)

        refreshListView()

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshListView()
    }

    private fun initListView(view: View) {
        listView = view.findViewById(R.id.list_view)
        listView!!.emptyView = view.findViewById(R.id.empty_text_view)
        listView!!.isTextFilterEnabled = true
        listView!!.onItemClickListener = OnItemClickListener { _, newView: View, _, _ ->
            onListItemClick(newView)
        }
        listView!!.adapter = adapter
    }

    private fun onListItemClick(newView: View) {
        val idTextView = newView.findViewById<TextView>(R.id.id_text_view)
        val presentId = idTextView.text.toString().toLong()
        val (id, name, translation, lvl, targetDate, modificationDate) = wordService.get(presentId)!!
        val modifyIntent = Intent(context, ModifyWordActivity::class.java)
        modifyIntent.putExtra(getString(R.string.id_key), id)
                .putExtra(getString(R.string.name_key), name)
                .putExtra(getString(R.string.translation_key), translation)
                .putExtra(getString(R.string.lvl_key), lvl)
                .putExtra(getString(R.string.target_date_time_key), targetDate.time)
                .putExtra(getString(R.string.modification_date_time_key), modificationDate.time)
        startActivity(modifyIntent)
    }

    private fun initSearch(view: View) {
        search = view.findViewById(R.id.search)
        search!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter!!.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun initFloatingButtons(view: View) {
        val addWordActionButton: FloatingActionButton = view.findViewById(R.id.add_word_action_button)
        addWordActionButton.setOnClickListener {
            val addWordIntent = Intent(context, AddWordActivity::class.java)
            startActivity(addWordIntent)
        }
        val wordSettingsActionButton: FloatingActionButton = view.findViewById(R.id.word_settings_action_button)
        wordSettingsActionButton.setOnClickListener {
            val addWordSettingsIntent = Intent(context, WordListSettingsActivity::class.java)
            startActivity(addWordSettingsIntent)
        }
    }

    private fun refreshListView() {
        refreshAdapter()
        listView!!.adapter = adapter
        val countOfWords = wordService.count()
        if (countOfWords == 0L) {
            wordCounterTextView!!.visibility = View.GONE
            listView!!.visibility = View.GONE
            search!!.visibility = View.GONE
        } else {
            wordCounterTextView!!.visibility = View.VISIBLE
            listView!!.visibility = View.VISIBLE
            search!!.visibility = View.VISIBLE
            wordCounterTextView!!.text = String.format("%s %d", super.getString(R.string.total_words_content), countOfWords)
        }
    }

    private fun refreshAdapter() {
        val words = wordService.findAll()
        adapter = WordToListViewAdapter(R.layout.activity_view_word, requireContext(), words)
    }

}