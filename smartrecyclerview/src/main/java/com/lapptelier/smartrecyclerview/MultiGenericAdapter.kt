package com.lapptelier.smartrecyclerview

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList
import java.util.HashMap
import kotlin.NoSuchElementException

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.MultiGenericAdapter
 *
 *
 * Generic implementation of a {@see SmartAdapter} that can manager multiple item type and their corresponding ViewHolders.
 *
 *
 * Sub class of [AbstractGenericAdapter].
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class MultiGenericAdapter

/**
 * Full constructor
 *
 * @param itemClass         class of an item in the list
 * @param viewHolderClass   associated view holde class for the given class
 * @param fragment_resource ressources_id of the layout for the given ViewHolder class
 */
(itemClass: Class<*>, viewHolderClass: Class<out ViewHolder>, fragment_resource: Int, listener: ViewHolderInteractionListener) : GenericViewHolderAdapter, RecyclerView.Adapter<ViewHolder>() {

    private val fragmentResources: MutableMap<Class<out ViewHolder>, Int> // List of all ViewHolders' layout ressource_id
    private val viewHolderForClass: MutableMap<Class<*>, Class<out ViewHolder>> // Map of all corresponding ViewHolders classes for a given item class
    private val viewHolders: MutableMap<Int, Class<out ViewHolder>> // Map of all the ViewHolders classes, by their layout ressource id
    val listener: ViewHolderInteractionListener // listener for viewHolder actions
    var items: ArrayList<Any>? = null // Adapter's idem list


    init {
        this.items = ArrayList()
        this.viewHolderForClass = HashMap()
        this.fragmentResources = HashMap()
        this.viewHolders = HashMap()
        this.listener = listener
    }

    init {
        this.addViewHolderType(itemClass, viewHolderClass, fragment_resource)
    }


    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(viewGroup: ViewGroup, index: Int): ViewHolder {

        if (index > -1) {
            //getting the ViewHolder class for a given item position
            val viewHolderClass = this.viewHolders[index]

            val view = LayoutInflater.from(viewGroup.context).inflate(fragmentResources.get(viewHolderClass)!!, viewGroup, false)

            //getting the constuctor of the ViewHolder by instropection
            try {
                val constructor = viewHolderClass!!.getConstructor(View::class.java)
                return constructor.newInstance(view) as ViewHolder
            } catch (exception: Exception) {
                Log.e("MultiGenericAdapter", "exception while creating viewHolder", exception)
            }

        }

        throw NoSuchElementException("No ViewHolder is defined for items at this index")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        (holder as SmartViewHolder<Any?>).setItem(items!![index], listener)
    }

    override fun getItemViewType(position: Int): Int {
        return this.fragmentResources[this.viewHolderForClass[this.items!![position].javaClass]]!!

    }

    // GenericViewHolderAdapter
    override fun addViewHolderType(itemClass: Class<*>, viewHolderClass: Class<out ViewHolder>, fragment_resource: Int) {
        this.viewHolderForClass[itemClass] = viewHolderClass
        this.fragmentResources[viewHolderClass] = fragment_resource
        this.viewHolders[fragment_resource] = viewHolderClass
    }

    // GenericViewHolderAdapter
    override fun removeViewHolderType(viewHolderClass: Class<out ViewHolder>) {
        if (viewHolderForClass.containsKey(viewHolderClass)) {
            this.viewHolderForClass.remove(viewHolderClass)
        }
        if (fragmentResources.containsKey(viewHolderClass)) {
            if (this.viewHolders.containsKey(fragmentResources[viewHolderClass]))
                this.viewHolders.remove(fragmentResources[viewHolderClass])
            this.fragmentResources.remove(viewHolderClass)
        }
    }

    /**
     * Return true if the list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    val isEmpty: Boolean
        get() = items != null && items!!.isEmpty()


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
        this.deletePlaceholder()
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
        this.deletePlaceholder()
        this.items!!.addAll(position, items)
        this.notifyItemInserted(position)
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
        this.deletePlaceholder()
        this.items!!.add(item)
        this.notifyItemRangeChanged(if (this.items!!.size > 1) this.items!!.size - 1 else 0, this.items!!.size)
    }

    /**
     * Wipe out the item list
     */
    fun clear() {
        val itemSize = this.itemCount
        this.items!!.clear()
        this.notifyItemRangeRemoved(0, itemSize)
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
     * @param item item to search in the list
     * @return item's position if found, -1 otherwise
     */
    fun getObjectIndex(item: Any): Int {
        var selectedIndex = -1
        this.items!!.indices.forEach { index ->
            if (this.items!![index] == item) {
                selectedIndex = index
            }
        }
        return selectedIndex
    }

    /**
     * Delete all placeholders
     */
    fun deletePlaceholder() {
        val selectedIndexes = ArrayList<Int>()
        this.items!!.indices.forEach { index ->
            if (this.items!![index] is PlaceHolderCell) {
                selectedIndexes.add(index)
            }
        }
        selectedIndexes.forEach {
            this.items!!.removeAt(it)
        }
    }

}
