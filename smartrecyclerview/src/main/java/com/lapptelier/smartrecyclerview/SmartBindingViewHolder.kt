package com.lapptelier.smartrecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * com.lapptelier.smartrecyclerview.SmartBindingViewHolder
 * <p/>
 * Abstract class defining a generic Smart ViewHolder using view binding for the {@see SmartRecyclerView}
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 02/09/2020
 */
abstract class SmartBindingViewHolder<T>(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    abstract fun setItem(
        item: T,
        listener: ViewHolderInteractionListener,
        recyclerViewPool: RecyclerView.RecycledViewPool?
    )
}