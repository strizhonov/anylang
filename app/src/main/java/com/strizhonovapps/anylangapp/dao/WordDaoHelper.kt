package com.strizhonovapps.anylangapp.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DB_NAME = "LEXIX_DB.DB"
const val DB_VERSION = 4
const val TABLE_NAME = "WORDS"

const val ID_INDEX = 0
const val NAME_INDEX = 1
const val TRANS_INDEX = 2
const val LVL_INDEX = 3
const val TARGET_DATE_INDEX = 4
const val MODIFICATION_DATE_INDEX = 5

const val ID = "_id"
const val NAME = "name"
const val TRANS = "translation"
const val LVL = "level"
const val TARGET_DATE = "targetDate"
const val MODIFICATION_DATE = "modifyDate"

class WordDaoHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION), AutoCloseable {

    private val createTableSql = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$NAME TEXT NOT NULL, $TRANS TEXT, $LVL INTEGER, $TARGET_DATE LONG, $MODIFICATION_DATE LONG);"

    override fun onCreate(db: SQLiteDatabase) = db.execSQL(createTableSql)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}