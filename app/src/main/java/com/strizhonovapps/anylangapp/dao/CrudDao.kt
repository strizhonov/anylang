package com.strizhonovapps.anylangapp.dao

import com.strizhonovapps.anylangapp.model.Word

interface CrudDao<T> {

    fun insert(entity: T): Long
    fun get(id: Long): Word?
    fun update(entity: T): Int
    fun delete(id: Long): Int
    fun findAll(): List<Word>
    fun erase(): Int
    fun size(): Long

}
