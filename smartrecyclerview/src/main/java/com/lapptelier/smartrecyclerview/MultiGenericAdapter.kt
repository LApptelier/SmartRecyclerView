package com.lapptelier.smartrecyclerview

import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.HashMap
import kotlin.NoSuchElementException
import kotlin.collections.set

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
class MultiGenericAdapter() : AbstractGenericAdapter<ViewHolder?>(), GenericViewHolderAdapter {


    private val fragmentResources: MutableMap<Class<out ViewHolder>, Int> // List of all ViewHolders' layout ressource_id
    private val viewHolderForClass: MutableMap<Class<*>, Class<out ViewHolder>> // Map of all corresponding ViewHolders classes for a given item class
    private val viewHolders: MutableMap<Int, Class<out ViewHolder>> // Map of all the ViewHolders classes, by their layout ressource id

    init {
        this.viewHolderForClass = HashMap()
        this.fragmentResources = HashMap()
        this.viewHolders = HashMap()
    }

    /**
     * Full constructor
     *
     * @param itemClass         class of an item in the list
     * @param viewHolderClass   associated view holde class for the given class
     * @param fragment_resource ressources_id of the layout for the given ViewHolder class
     */
    constructor(itemClass: Class<*>, viewHolderClass: Class<out ViewHolder>, fragment_resource: Int) : this() {
        this.addViewHolderType(itemClass, viewHolderClass, fragment_resource)
    }


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
        (holder as MyViewHolder<Any?>).setItem(items!![index])
    }

    override fun getItemViewType(position: Int): Int {
        return this.fragmentResources[this.viewHolderForClass[this.items!![position].javaClass]]!!

    }

    override fun addViewHolderType(itemClass: Class<*>, viewHolderClass: Class<out ViewHolder>, fragment_resource: Int) {
        this.viewHolderForClass[itemClass] = viewHolderClass
        this.fragmentResources[viewHolderClass] = fragment_resource
        this.viewHolders[fragment_resource] = viewHolderClass
    }

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


}
