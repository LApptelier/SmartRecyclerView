package com.lapptelier.smartrecyclerview

/**
 * com.lapptelier.smartrecyclerview.IAdapterInteraction
 *
 * This interface must be implemented by activities or fragment that contain this
 * recycler view to allow an interaction with the adapter
 *
 *
 * @author L'Apptelier SARL
 * @date 23/08/2018
 */
interface ViewHolderInteractionListener {
    /**
     * Listener for all type of actions interaction with a viewHolder
     *
     * @param item the item corresponding to the touched cell
     * @param viewId id of the touched view in the cell
     * @param action action excecuted on the cell, see [ViewHolderInteraction]
     */
    fun onItemAction(item: Any, viewId: Int, viewHolderInteraction: ViewHolderInteraction)
}

