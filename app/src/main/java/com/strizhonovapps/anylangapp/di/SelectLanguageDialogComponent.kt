package com.strizhonovapps.anylangapp.di

import com.strizhonovapps.anylangapp.view.SelectLanguageDialog
import dagger.Component
import javax.inject.Singleton

@FunctionalInterface
@Singleton
@Component(modules = [WordServiceModule::class])
interface SelectLanguageDialogComponent {

    fun inject(dialog: SelectLanguageDialog)

}
