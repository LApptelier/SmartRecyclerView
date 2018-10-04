package com.lapptelier.smartrecyclerview

/**
 * com.lapptelier.smartrecyclerview.wrapper
 *
 *
 * Subclass of LinearLayoutManager. Correct a bug that can occurs with multiple ViewHolders
 *
 *
 *
 * @author L'Apptelier SARL
 * @date 21/02/2018
 * @see LinearLayoutManager
 *
 * @see RecyclerView
 *
 * @see https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in.33822747
 *
 *
 */

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log

class WrapContentLinearLayoutManager : androidx.recyclerview.widget.LinearLayoutManager {

    /**
     * Constructors
     */
    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler, state: androidx.recyclerview.widget.RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.d("WrapContentLinearLayout", "Meet a IOOBE in RecyclerView", e)
        }

    }
}
