package com.lapptelier.smartrecyclerview

import android.view.View

/**
 * com.lapptelier.smartrecyclerview.OnItemClickListener
 *
 *
 * This interface allows the implementer to receive click event on a given {@see SmartRecyclerView}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
}
