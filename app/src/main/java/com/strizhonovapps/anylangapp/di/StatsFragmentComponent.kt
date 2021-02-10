package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.StatsFragment
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface StatsFragmentComponent {

    fun inject(fragment: StatsFragment)

}
