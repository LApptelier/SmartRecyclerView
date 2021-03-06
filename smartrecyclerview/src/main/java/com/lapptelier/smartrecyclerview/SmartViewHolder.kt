package com.lapptelier.smartrecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.MyViewHolder
 *
 *
 * Abstract class defining a generic Smart ViewHolder for the {@see SmartRecyclerView}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
abstract class SmartViewHolder<T>(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    abstract fun setItem(item: T, listener: ViewHolderInteractionListener)
}
