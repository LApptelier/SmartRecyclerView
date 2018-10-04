package com.lapptelier.smartrecyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 * com.lapptelier.smartrecyclerview.GenericViewHolderAdapter
 *
 *
 * Interface to deal with multi generic view holder in an adapter
 *
 *
 *
 * @author L'Apptelier SARL
 * @date 23/10/2017
 */
internal interface GenericViewHolderAdapter {

    /**
     * Add a new ViewHolder class for an item class
     *
     * @param itemClass         class of an item in the list
     * @param viewHolderClass   associated view holde class for the given class
     * @param fragment_resource ressources_id of the layout for the given ViewHolder class
     */
    fun addViewHolderType(itemClass: Class<*>, viewHolderClass: Class<out androidx.recyclerview.widget.RecyclerView.ViewHolder>, fragment_resource: Int)

    /**
     * Remove a ViewHolder class
     *
     * @param viewHolderClass ViewHolder class to remove
     */
    fun removeViewHolderType(viewHolderClass: Class<out androidx.recyclerview.widget.RecyclerView.ViewHolder>)


}
