package com.lapptelier.smartrecyclerview;

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.MyViewHolder
 * <p/>
 * Interface defining a generic ViewHolder for the {@see SmartRecyclerView}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public interface MyViewHolder<T> {

    void setItem(final T t);
}
