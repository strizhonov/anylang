package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.AddWordActivity
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface AddWordActivityComponent {

    fun inject(activity: AddWordActivity)

}
