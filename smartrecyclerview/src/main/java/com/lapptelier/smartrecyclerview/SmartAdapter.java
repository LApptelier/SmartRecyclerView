package com.lapptelier.smartrecyclerview;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.SmartAdapter
 * <p/>
 * Generic implementation of a {@see RecyclerView.Adapter}.
 * <p/>
 * Works with any object type.
 * <p/>
 * Sub class of {@link RecyclerView.Adapter}.
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public abstract class SmartAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    public List items; // Adapter's idem list

    /**
     * Empty Constructor
     */
    public SmartAdapter() {
        this.items = new ArrayList();
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    /**
     * Return true if the list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return (items != null && items.isEmpty());
    }

    /**
     * Wipe out the item list
     */
    public void clear() {
        this.items.clear();
    }

    /**
     * Add all the given items to the item list.
     *
     * @param items items to add
     */
    public void addAll(List<?> items) {
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    /**
     * Insert a given item at a given position in the list
     *
     * @param item     item to insert
     * @param position position to insert to
     */
    public void insertAt(int position, Object item) {
        if (position < items.size()) {
            this.items.add(position, item);
        } else {
            this.items.add(item);
        }
        this.notifyDataSetChanged();
    }

    /**
     * Delete an item from the list
     *
     * @param position position of the item to delete
     */
    public void removeAt(int position) {
        if (position < items.size()) {
            this.items.remove(position);
            this.notifyDataSetChanged();
        }
    }

    /**
     * Replace an item at a given position by another given item
     *
     * @param position position of the item to replace
     * @param item     new item
     */
    public void replaceAt(int position, Object item) {
        if (position < items.size()) {
            this.items.set(position, item);
            this.notifyDataSetChanged();
        }
    }

    /**
     * Return the item at the given position
     *
     * @param position position of the item
     *                 @return the item at the given position, null otherwise
     */
    public Object getItemAt(int position) {
        if (position < 0 || position > this.items.size() - 1) {
            return null;
        } else {
            return this.items.get(position);
        }
    }

    /**
     * Append an item to the list
     *
     * @param item item to append
     */
    public void add(Object item) {
        this.items.add(item);
    }

    /**
     * Return true if the given object is already in the item list
     *
     * @param item item to test
     * @return true if the given object is already in the item list, false otherwise
     */
    public boolean contains(Object item) {
        return this.items.contains(item);
    }


    /**
     * Return the position in the list of the given item
     *
     * @param object item to search in the list
     * @return item's position if found, -1 otherwise
     */
    public int getObjectIndex(Object object) {
        int selectedIndex = -1;
        for (int index = 0; index < this.items.size(); index++) {
            Object item = this.items.get(index);
            if (item != null)
                if (item.equals(object)) {
                    selectedIndex = index;
                }
        }
        return selectedIndex;
    }
}
