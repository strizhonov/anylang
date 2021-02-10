package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.WordListFragment
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface WordListFragmentComponent {

    fun inject(fragment: WordListFragment)

}
