package com.lapptelier.smartrecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * com.lapptelier.smartrecyclerview.GenericViewHolderAdapter
 * <p/>
 * Interface to deal with multi generic view holder in an adapter
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 23/10/2017
 */
interface GenericViewHolderAdapter {

    /**
     * Add a new ViewHolder class for an item class
     *
     * @param itemClass         class of an item in the list
     * @param viewHolderClass   associated view holde class for the given class
     * @param fragment_resource ressources_id of the layout for the given ViewHolder class
     */
    void addViewHolderType(Class<?> itemClass, Class<? extends RecyclerView.ViewHolder> viewHolderClass, int fragment_resource);

    /**
     * Remove a ViewHolder class
     *
     * @param viewHolderClass ViewHolder class to remove
     */
    void removeViewHolderType(Class<? extends RecyclerView.ViewHolder> viewHolderClass);


}
