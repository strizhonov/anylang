package com.strizhonovapps.anylangapp.viewsupport

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.view.WordListSettingsActivity
import java.io.InputStream
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class SettingsDialogFactory(private val activity: WordListSettingsActivity,
                            private val context: Context,
                            private val wordService: WordService) {

    fun getEraseConfirmationDialog(): AlertDialog = AlertDialog.Builder(activity)
            .setTitle(context.getString(R.string.clear_all_title))
            .setMessage(context.getString(R.string.are_you_sure_content))
            .setPositiveButton(context.getString(R.string.yes_content)) { _: DialogInterface?, _: Int ->
                wordService.erase()
                activity.finish()
            }
            .setNegativeButton(context.getString(R.string.cancel_content), null)
            .create()

    fun getSeparatorDialog(inputStream: InputStream?): AlertDialog {
        val separatorEditText = EditText(activity)
        return AlertDialog.Builder(activity)
                .setTitle(context.getString(R.string.insert_separator_title))
                .setMessage(context.getString(R.string.separator_message_content))
                .setView(separatorEditText)
                .setPositiveButton(context.getString(R.string.done_content)) { _: DialogInterface?, _: Int ->
                    val separator = separatorEditText.text.toString()
                    Callable {
                        try {
                            wordService.saveAllFromStream(inputStream!!, separator)
                            activity.finish()
                        } catch (e: Exception) {
                            Log.e(this.javaClass.simpleName, "Unable to save words from file.", e)
                            Toast.makeText(context,
                                    context.getString(R.string.unable_to_save_from_file_message),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }.also { Executors.newSingleThreadExecutor().submit(it) }
                }
                .setNegativeButton(context.getString(R.string.cancel_content), null)
                .create()
    }

    fun getResetProgressDialog(): AlertDialog = AlertDialog.Builder(activity)
            .setTitle(context.getString(R.string.reset_progress_content))
            .setMessage(context.getString(R.string.are_you_sure_content))
            .setPositiveButton(context.getString(R.string.yes_content)) { _: DialogInterface?, _: Int ->
                wordService.resetProgress()
                activity.finish()
            }
            .setNegativeButton(context.getString(R.string.cancel_content), null)
            .create()

}