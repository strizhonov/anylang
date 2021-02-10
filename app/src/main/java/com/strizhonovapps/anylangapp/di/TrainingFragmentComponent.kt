package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.TrainingFragment
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface TrainingFragmentComponent {

    fun inject(fragment: TrainingFragment)

}