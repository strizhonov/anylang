package com.strizhonovapps.anylangapp.viewsupport

import android.app.AlertDialog

interface LanguageDialogFactory {
    fun getDialog(): AlertDialog
}