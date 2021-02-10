package com.strizhonovapps.anylangapp.viewsupport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.model.Word
import java.util.*

class WordToListViewAdapter(resource: Int,
                            context: Context,
                            words: List<Word>) : ArrayAdapter<Word>(context, resource, words) {

    private var originalValues: MutableList<Word> = ArrayList(words)
    private var displayedValues: MutableList<Word> = ArrayList(words)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var tempView = convertView
        if (tempView == null) {
            tempView = getTempView(parent)
            viewHolder = getViewHolder(tempView)
            tempView.tag = viewHolder
        } else {
            viewHolder = tempView.tag as ViewHolder
        }
        inflateViewHolder(position, viewHolder)
        return tempView
    }

    override fun getCount() = displayedValues.size
    override fun getItem(position: Int) = displayedValues[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getFilter() = WordFilter(this, displayedValues, originalValues, context)

    private fun inflateViewHolder(position: Int, viewHolder: ViewHolder) {
        val current = this.getItem(position)
        val sPosition: String = (position + 1).toString()
        viewHolder.position?.text = sPosition
        viewHolder.id?.text = current.id.toString()
        viewHolder.name?.text = current.name
        viewHolder.translation?.text = current.translation
        viewHolder.lvl?.text = String.format("%s %d", context.getString(R.string.level_content), current.lvl)
    }

    private fun getViewHolder(tempView: View): ViewHolder {
        val viewHolder = ViewHolder()
        viewHolder.id = tempView.findViewById(R.id.id_text_view)
        viewHolder.position = tempView.findViewById(R.id.position_text_view)
        viewHolder.name = tempView.findViewById(R.id.name_text_view)
        viewHolder.translation = tempView.findViewById(R.id.translation_text_view)
        viewHolder.lvl = tempView.findViewById(R.id.level_text_view)
        return viewHolder
    }

    private fun getTempView(parent: ViewGroup) = LayoutInflater.from(context).inflate(R.layout.activity_view_word, parent, false)!!

    /**
     * Cache of view
     */
    private data class ViewHolder(
            var id: TextView? = null,
            var position: TextView? = null,
            var name: TextView? = null,
            var translation: TextView? = null,
            var lvl: TextView? = null,
    )

}