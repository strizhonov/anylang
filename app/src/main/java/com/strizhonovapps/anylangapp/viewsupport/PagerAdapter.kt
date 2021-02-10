package com.strizhonovapps.anylangapp.viewsupport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.strizhonovapps.anylangapp.view.StatsFragment
import com.strizhonovapps.anylangapp.view.TrainingFragment
import com.strizhonovapps.anylangapp.view.WordListFragment

class PagerAdapter(fm: FragmentManager, private val numOfTabs: Int)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val training = TrainingFragment()
        val wordList = WordListFragment()
        val stats = StatsFragment()

        return when (position) {
            1 -> wordList
            2 -> stats
            0 -> training
            else -> training
        }
    }

    override fun getCount() = numOfTabs

}