package com.lapptelier.smartrecyclerview

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.PlaceHolderViewHolder
 *
 *
 * ViewHolder of a generic placeholder
 *
 * Sub class of [RecyclerView.ViewHolder]
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class PlaceHolderViewHolder(view: View) : SmartViewHolder<PlaceHolderCell>(view) {
    override fun setItem(item: PlaceHolderCell, listener: ViewHolderInteractionListener) {
        //unused
    }
}
