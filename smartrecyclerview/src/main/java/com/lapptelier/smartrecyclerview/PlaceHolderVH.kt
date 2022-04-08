package com.lapptelier.smartrecyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * com.msd.msdetmoi.common.util.new_smart_recycler_view.PlaceHolderViewHolder
 * <p/>
 * Implementation of a viewholder for placeholder cell
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 02/09/2020
 */
class PlaceHolderVH(binding: ViewBinding) : SmartBindingViewHolder<PlaceHolderCell>(binding.root) {
    override fun setItem(item: PlaceHolderCell, listener: ViewHolderInteractionListener, recyclerViewPool: RecyclerView.RecycledViewPool?) {
    }
}
