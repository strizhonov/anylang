package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.ModifyWordActivity
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface ModifyWordActivityComponent {

    fun inject(activity: ModifyWordActivity)

}
