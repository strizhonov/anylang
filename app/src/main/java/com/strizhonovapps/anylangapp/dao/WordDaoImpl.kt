package com.strizhonovapps.anylangapp.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.strizhonovapps.anylangapp.model.Word
import java.util.*

class WordDaoImpl(private val context: Context) : WordDao {

    override fun insert(entity: Word) = WordDaoHelper(context).use { dbHelper ->
        dbHelper.writableDatabase.use { database ->
            val contentValues = getContentValuesFromWord(entity)
            database.insert(TABLE_NAME, null, contentValues)
        }
    }

    override fun get(id: Long) = getCursorAndProcessWith(ID) { cursor ->
        while (cursor.moveToNext()) {
            if (id == cursor.getLong(ID_INDEX)) {
                return@getCursorAndProcessWith Word(
                        id,
                        cursor.getString(NAME_INDEX),
                        cursor.getString(TRANS_INDEX),
                        cursor.getInt(LVL_INDEX),
                        Date(cursor.getLong(TARGET_DATE_INDEX)),
                        Date(cursor.getLong(MODIFICATION_DATE_INDEX)))
            }
        }
        return@getCursorAndProcessWith null
    }

    override fun update(entity: Word) = WordDaoHelper(context).use { dbHelper ->
        dbHelper.writableDatabase.use { database ->
            val contentValues = getContentValuesFromWord(entity)
            database.update(TABLE_NAME, contentValues, "$ID=${entity.id}", null)
        }
    }

    override fun delete(id: Long) = WordDaoHelper(context).use { dbHelper ->
        dbHelper.writableDatabase.use { database ->
            database.delete(TABLE_NAME, "$ID=$id", null)
        }
    }

    override fun findAll(): List<Word> {
        val words: MutableList<Word> = ArrayList()
        getCursorAndProcessWith(ID) { cursor -> fillWordListFromCursor(words, cursor) }
        return words
    }

    override fun erase() = WordDaoHelper(context).use { dbHelper ->
        dbHelper.writableDatabase.use { database ->
            database.delete(TABLE_NAME, null, null)
        }
    }

    override fun size() = getCursorAndProcessWith(ID) { cursor -> cursor.count.toLong() }

    override fun findAllSortedByTargetDate(): List<Word> {
        val words: MutableList<Word> = ArrayList()
        getCursorAndProcessWith(TARGET_DATE) { cursor -> fillWordListFromCursor(words, cursor) }
        return words
    }

    private fun <R> getCursorAndProcessWith(sortBy: String?, block: (Cursor) -> R): R {
        WordDaoHelper(context).use { wordDaoHelper ->
            wordDaoHelper.writableDatabase.use { writableDatabase ->
                return fetch(writableDatabase, sortBy).use { cursor -> block.invoke(cursor) }
            }
        }
    }

    private fun fetch(writableDatabase: SQLiteDatabase, sortBy: String?): Cursor {
        val columns = arrayOf(ID, NAME, TRANS, LVL, TARGET_DATE, MODIFICATION_DATE)
        return writableDatabase.query(TABLE_NAME, columns, null, null, null, null, sortBy)
    }

    private fun getContentValuesFromWord(entity: Word): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(ID, entity.id)
        contentValues.put(NAME, entity.name)
        contentValues.put(TRANS, entity.translation)
        contentValues.put(LVL, entity.lvl)
        contentValues.put(TARGET_DATE, entity.targetDate.time)
        contentValues.put(MODIFICATION_DATE, entity.modificationDate.time)
        return contentValues
    }

    private fun fillWordListFromCursor(words: MutableList<Word>, cursor: Cursor?) {
        while (cursor!!.moveToNext()) {
            val word = Word(
                    cursor.getLong(ID_INDEX),
                    cursor.getString(NAME_INDEX),
                    cursor.getString(TRANS_INDEX),
                    cursor.getInt(LVL_INDEX),
                    Date(cursor.getLong(TARGET_DATE_INDEX)),
                    Date(cursor.getLong(MODIFICATION_DATE_INDEX)))
            words.add(word)
        }
    }

}