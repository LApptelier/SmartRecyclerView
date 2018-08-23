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
     * Listener for click interaction with a viewHolder
     */
    fun onItemClicked(item: Any)

    /**
     * Listener for all type of actions interaction with a viewHolder
     */
    fun onItemAction(item: Any, action: Int)
}