package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.WordListSettingsActivity
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface WordListSettingsActivityComponent {

    fun inject(activity: WordListSettingsActivity)

}
