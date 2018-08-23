package com.lapptelier.smartrecyclerview

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.MyViewHolder
 *
 *
 * Interface defining a generic ViewHolder for the {@see SmartRecyclerView}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
interface SmartViewHolder<T> {

    fun setItem(item: T, listener: ViewHolderInteractionListener)
}
