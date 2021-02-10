package com.strizhonovapps.anylangapp.service

import android.util.Log

private const val START_INDEX = -1

class CircleListIterator<T>(private val toIterate: List<T>) : Iterator<T> {

    private var index = START_INDEX

    override fun hasNext() = toIterate.isNotEmpty()

    override fun next(): T {
        if (toIterate.isEmpty()) {
            Log.e(this.javaClass.simpleName, "Unable to perform element retrieving from empty collection.")
            throw IndexOutOfBoundsException("Unable to perform element retrieving from empty collection.")
        }
        increaseIndex()
        return toIterate[index]
    }

    fun hasPrevious() = toIterate.isNotEmpty()

    fun previous(): T {
        if (toIterate.isEmpty()) {
            Log.e(this.javaClass.simpleName, "Unable to perform element retrieving from empty collection.")
            throw IndexOutOfBoundsException("Unable to perform element retrieving from empty collection.")
        }
        decreaseIndex()
        return toIterate[index]
    }

    private fun increaseIndex() {
        if (++index == toIterate.size) {
            index = 0
        }
    }

    private fun decreaseIndex() {
        if (--index < 0) {
            index = toIterate.size - 1
        }
    }

}