package com.lapptelier.smartrecyclerview

import androidx.recyclerview.widget.RecyclerView
import com.lapptelier.smartrecyclerview.PlaceHolderCell
import com.lapptelier.smartrecyclerview.ViewHolderInteractionListener

/**
 * com.lapptelier.smartrecyclerview.GenericAdapter
 * <p/>
 * Generic adapter
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 02/09/2020
 */
abstract class GenericAdapter(val listener: ViewHolderInteractionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: ArrayList<Any> = ArrayList() // Adapter's idem list
    var hasMore: Boolean = false // Flag indicating if there are more item to load

    /**
     * Return true if the list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    val isEmpty: Boolean
        get() = items.isEmpty()


    /**
     * Return the number of items in this adapter
     * @return the number of items in this adapter
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Clear the list and add the given item
     *
     * @param item item to append
     */
    fun add(item: Any) {
        this.items = arrayListOf(item)
        if (hasMore) {
            this.items.add(PlaceHolderCell())
            this.notifyItemRangeInserted(0, 2)
        }
        this.notifyItemRangeInserted(0, 1)
    }

    /**
     * Append all the given items to the item list.
     *
     * @param items items to append
     */
    fun addAll(items: List<Any>) {
        if (items.isEmpty()) {
            this.clear()
        } else {
            val tempList: ArrayList<Any> = ArrayList()
            tempList.addAll(items)
            if (hasMore) {
                tempList.add(PlaceHolderCell())
            }
            this.items = tempList
        }
        this.notifyDataSetChanged()
    }

    /**
     * Append an item to the list
     *
     * @param item item to append
     */
    fun append(item: Any) {
        this.deletePlaceholder()
        val index = this.items.size
        this.items.add(item)
        if (hasMore) {
            this.items.add(PlaceHolderCell())
            this.notifyItemRangeInserted(index, 2)
        } else
            this.notifyItemInserted(index)
    }

    /**
     * Append all the given items to the item list.
     *
     * @param items items to append
     */
    fun appendAll(items: List<Any>) {
        this.deletePlaceholder()
        val index = this.items.size
        if (items.isEmpty()) {
            this.notifyDataSetChanged()
        } else {
            this.items.addAll(items)
            if (hasMore)
                this.items.add(PlaceHolderCell())
            this.notifyItemRangeInserted(index, items.size)
        }
    }

    /**
     * Add all the given items to the item list.
     *
     * @param items    items to append
     * @param position position to insert elements into
     */
    fun insertAll(items: List<Any>, position: Int) {
        if (items.isEmpty()) {
            this.notifyDataSetChanged()
        } else {
            if (position < items.size) {
                this.items.addAll(position, items)
                this.notifyItemRangeInserted(position, items.size)
            } else {
                this.items.addAll(items)
                this.notifyItemRangeInserted(this.items.count(), items.size)
            }
            this.items.addAll(position, items)
            this.notifyItemRangeInserted(position, items.size)
        }
    }

    /**
     * Insert a given item at a given position in the list
     *
     * @param item     item to insert
     * @param position position to insert to
     */
    fun insertAt(position: Int, item: Any) {
        if (position < items.size) {
            this.items.add(position, item)
            this.notifyItemInserted(position)
        } else {
            this.items.add(item)
            this.notifyItemInserted(this.items.count())
        }
    }

    /**
     * Delete an item from the list
     *
     * @param position position of the item to delete
     */
    fun removeAt(position: Int) {
        if (position < items.size) {
            this.items.removeAt(position)
            this.notifyItemRemoved(position)
        } else {
            this.notifyDataSetChanged()
        }
    }

    /**
     * Replace an item at a given position by another given item
     *
     * @param position position of the item to replace
     * @param item     new item
     */
    fun replaceAt(position: Int, item: Any) {
        if (position < items.size) {
            this.items.set(position, item)
            this.notifyItemChanged(position, item)
        } else {
            this.notifyDataSetChanged()
        }
    }

    /**
     * Return the item at the given position
     *
     * @param position position of the item
     * @return the item at the given position, null otherwise
     */
    fun getItemAt(position: Int): Any? {
        return if (position < 0 || position > this.items.size - 1) {
            null
        } else {
            this.items[position]
        }
    }


    /**
     * Wipe out the item list
     */
    fun clear() {
        this.items.clear()
        this.notifyDataSetChanged()
    }

    /**
     * Return true if the given object is already in the item list
     *
     * @param item item to test
     * @return true if the given object is already in the item list, false otherwise
     */
    operator fun contains(item: Any): Boolean {
        return this.items.contains(item)
    }


    /**
     * Return the position in the list of the given item
     *
     * @param item item to search in the list
     * @return item's position if found, -1 otherwise
     */
    fun getObjectIndex(item: Any): Int {
        var selectedIndex = -1
        this.items.indices.forEach { index ->
            if (this.items[index] == item) {
                selectedIndex = index
            }
        }
        return selectedIndex
    }

    /**
     * Delete all placeholders
     */
    private fun deletePlaceholder() {
        val selectedIndexes = ArrayList<Int>()
        this.items.indices.forEach { index ->
            if (this.items[index] is PlaceHolderCell) {
                selectedIndexes.add(index)
            }
        }
        selectedIndexes.forEach { index ->
            this.items.removeAt(index)
            this.notifyItemRemoved(index)
        }
    }
}