package com.strizhonovapps.anylangapp.dao

import com.strizhonovapps.anylangapp.model.Word

interface WordDao : CrudDao<Word> {

    /**
     * Return word that has the earliest target date value.
     *
     * @return word that has the earliest target date value.
     */
    fun findAllSortedByTargetDate(): List<Word>

}
