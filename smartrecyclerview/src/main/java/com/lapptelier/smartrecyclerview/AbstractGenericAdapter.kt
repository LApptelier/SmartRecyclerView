package com.lapptelier.smartrecyclerview

import android.support.v7.widget.RecyclerView
import java.util.*


/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.AbstractGenericAdapter
 *
 *
 * Generic implementation of a {@see RecyclerView.Adapter}.
 *
 *
 * Works with any object type.
 *
 *
 * Sub class of [RecyclerView.Adapter].
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
abstract class AbstractGenericAdapter<V : RecyclerView.ViewHolder?> : RecyclerView.Adapter<V>() {

    var items: ArrayList<Any>? = null // Adapter's idem list

    /**
     * Return true if the list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    val isEmpty: Boolean
        get() = items != null && items!!.isEmpty()

    /**
     * Empty Constructor
     */
    init {
        this.items = ArrayList()
    }

    /**
     * Wipe out the item list
     */
    fun clear() {
        this.items!!.clear()
    }

    /**
     * Return the number of items in this adapter
     * @return the number of items in this adapter
     */
    override fun getItemCount(): Int {
        return if (items != null) {
            items!!.size
        } else {
            0
        }
    }


    /**
     * Add all the given items to the item list.
     *
     * @param items items to add
     */
    fun addAll(items: List<Any>) {
        this.items!!.addAll(items)
        this.notifyDataSetChanged()
    }

    /**
     * Add all the given items to the item list.
     *
     * @param items    items to add
     * @param position position to insert elements into
     */
    fun insertAll(items: List<Any>, position: Int) {
        this.items!!.addAll(position, items)
        this.notifyDataSetChanged()
    }

    /**
     * Insert a given item at a given position in the list
     *
     * @param item     item to insert
     * @param position position to insert to
     */
    fun insertAt(position: Int, item: Any) {
        if (position < items!!.size) {
            this.items!!.add(position, item)
        } else {
            this.items!!.add(item)
        }
        this.notifyItemInserted(position)
    }

    /**
     * Delete an item from the list
     *
     * @param position position of the item to delete
     */
    fun removeAt(position: Int) {
        if (position < items!!.size) {
            this.items!!.removeAt(position)
            this.notifyItemRemoved(position)
        }
    }

    /**
     * Replace an item at a given position by another given item
     *
     * @param position position of the item to replace
     * @param item     new item
     */
    fun replaceAt(position: Int, item: Any) {
        if (position < items!!.size) {
            this.items!!.set(position, item)
            this.notifyItemChanged(position, item)
        }
    }

    /**
     * Return the item at the given position
     *
     * @param position position of the item
     * @return the item at the given position, null otherwise
     */
    fun getItemAt(position: Int): Any? {
        return if (position < 0 || position > this.items!!.size - 1) {
            null
        } else {
            this.items!![position]
        }
    }

    /**
     * Append an item to the list
     *
     * @param item item to append
     */
    fun add(item: Any) {
        this.items!!.add(item)
        this.notifyItemRangeChanged(if (this.items!!.size > 1) this.items!!.size - 1 else 0, this.items!!.size)
    }

    /**
     * Return true if the given object is already in the item list
     *
     * @param item item to test
     * @return true if the given object is already in the item list, false otherwise
     */
    operator fun contains(item: Any): Boolean {
        return this.items!!.contains(item)
    }


    /**
     * Return the position in the list of the given item
     *
     * @param object item to search in the list
     * @return item's position if found, -1 otherwise
     */
    fun getObjectIndex(`object`: Any): Int {
        var selectedIndex = -1
        for (index in this.items!!.indices) {
            val item = this.items!![index]
            if (item != null)
                if (item == `object`) {
                    selectedIndex = index
                }
        }
        return selectedIndex
    }
}
