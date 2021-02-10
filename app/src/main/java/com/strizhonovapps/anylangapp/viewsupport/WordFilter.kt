package com.strizhonovapps.anylangapp.viewsupport

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.model.Word
import java.util.*

class WordFilter(private val arrayAdapter: ArrayAdapter<Word>,
                 private val displayedValues: MutableList<Word>,
                 private val originalValues: MutableList<Word>,
                 private val context: Context) : Filter() {

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        displayedValues.clear()
        safeAddAll(results.values)
        arrayAdapter.notifyDataSetChanged()
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        if (originalValues.isEmpty()) {
            originalValues.addAll(displayedValues)
        }
        if (constraint.isEmpty()) {
            return getNonFilteredResults()
        }
        return filterResults(constraint)
    }

    private fun filterResults(constraint: CharSequence): FilterResults {
        val filteredList: MutableList<Word> = ArrayList()
        val results = FilterResults()
        for (i in originalValues.indices) {
            val current: Word = originalValues[i]
            val valueToCompare = "${current.name} ${current.translation} ${context.getString(R.string.level_content)} ${current.lvl}"
            if (doesWordsContainsConstraint(valueToCompare, constraint)) {
                filteredList.add(current)
            }
        }
        results.count = filteredList.size
        results.values = filteredList
        return results
    }

    private fun getNonFilteredResults(): FilterResults {
        val results = FilterResults()
        results.count = originalValues.size
        results.values = originalValues
        return results
    }

    private fun doesWordsContainsConstraint(valueToCompare: String, constraint: CharSequence) =
            valueToCompare.toLowerCase(Locale.ROOT)
                    .contains(constraint.toString().toLowerCase(Locale.ROOT))

    private fun safeAddAll(values: Any?) {
        if (values is Collection<*>) {
            values.forEach { v ->
                run {
                    if (v is Word) {
                        displayedValues.add(v)
                    }
                }
            }
        }
    }

}