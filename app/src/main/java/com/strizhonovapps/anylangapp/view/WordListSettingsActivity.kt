package com.strizhonovapps.anylangapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.strizhonovapps.anylangapp.R
import com.strizhonovapps.anylangapp.di.DaggerWordListSettingsActivityComponent
import com.strizhonovapps.anylangapp.di.WordServiceModule
import com.strizhonovapps.anylangapp.service.WordService
import com.strizhonovapps.anylangapp.viewsupport.SettingsDialogFactory
import java.io.InputStream
import javax.inject.Inject

private const val TRANSFER_REQ_CODE = 101

class WordListSettingsActivity : Activity(), View.OnClickListener {

    @Inject
    lateinit var wordService: WordService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_word_list_settings)

        DaggerWordListSettingsActivityComponent.builder()
                .wordServiceModule(WordServiceModule(this))
                .build()
                .inject(this)

        title = super.getString(R.string.options_content)
        findViewById<Button>(R.id.clear_list_button).setOnClickListener(this)
        findViewById<Button>(R.id.delete_duplicates_button).setOnClickListener(this)
        findViewById<Button>(R.id.sync_button).setOnClickListener(this)
        findViewById<Button>(R.id.transfer_button).setOnClickListener(this)
        findViewById<Button>(R.id.trim_words_button).setOnClickListener(this)
        findViewById<Button>(R.id.reset_progress_button).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        hide()
        when (v.id) {
            R.id.clear_list_button -> {
                SettingsDialogFactory(this, applicationContext, wordService).getEraseConfirmationDialog().show()
                show()
            }
            R.id.reset_progress_button -> {
                SettingsDialogFactory(this, applicationContext, wordService).getResetProgressDialog().show()
                show()
            }
            R.id.delete_duplicates_button -> {
                wordService.removeDuplicates()
                finish()
            }
            R.id.sync_button -> {
                wordService.syncAllTranslation()
                finish()
            }
            R.id.transfer_button -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "text/plain"
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_file_content)),
                        TRANSFER_REQ_CODE)
            }
            R.id.trim_words_button -> {
                wordService.trimAndLowerCaseAllWords()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TRANSFER_REQ_CODE -> showFileUploadingDialogOrToast(data)
        }
    }

    override fun onResume() {
        super.onResume()
        show()
    }

    private fun showFileUploadingDialogOrToast(data: Intent?) {
        try {
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    val inputStream: InputStream = this.contentResolver.openInputStream(uri)!!
                    val dialog = SettingsDialogFactory(this, applicationContext, wordService).getSeparatorDialog(inputStream)
                    dialog.show()
                }
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.simpleName, "Unable to save words from file.", e)
            Toast.makeText(applicationContext,
                    getString(R.string.unable_to_save_from_file_message),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun hide() {
        this.title = this.getString(R.string.wait_content)
        this.findViewById<View>(R.id.clear_list_button).visibility = View.GONE
        this.findViewById<View>(R.id.delete_duplicates_button).visibility = View.GONE
        this.findViewById<View>(R.id.sync_button).visibility = View.GONE
        this.findViewById<View>(R.id.transfer_button).visibility = View.GONE
        this.findViewById<View>(R.id.trim_words_button).visibility = View.GONE
        this.findViewById<View>(R.id.reset_progress_button).visibility = View.GONE
        this.findViewById<View>(R.id.progress_bar).visibility = View.VISIBLE
    }

    private fun show() {
        this.findViewById<View>(R.id.clear_list_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.delete_duplicates_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.sync_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.transfer_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.trim_words_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.reset_progress_button).visibility = View.VISIBLE
        this.findViewById<View>(R.id.progress_bar).visibility = View.GONE
    }

}