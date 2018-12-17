package com.lapptelier.smartrecyclerview

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_smart_recycler_view.view.*
import java.util.*


/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.SmartRecyclerView
 *
 *
 * Generic implementation of a {@see RecyclerView} intagrated in {@SwipeRefreshLayout} layout.
 *
 *
 * Feature placeholder for empty list and loading progress bar both during reloading and loading more items.
 *
 *
 * Sub class of [LinearLayout].
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class SmartRecyclerView : LinearLayout {

    // count of item to display before firing the loadmore action
    private var itemLeftMoreToLoad = 10

    // actual recycler view
    var recyclerView: RecyclerView? = null

    // swipe layout
    var swipeLayout: SwipeRefreshLayout? = null

    // placeholder on the loading of the list
    var mLoadingViewStub: ViewStub? = null
    var mEmptyViewStub: ViewStub? = null

    // placeholder cell used to display load more progress
    private val placeHolderCell = PlaceHolderCell()

    /**
     * Return the inflated view layout placed when the list is loading elements
     *
     * @return the 'loading' view
     */
    var loadingView: View? = null
        private set
    /**
     * Return the inflated view layout placed when the list is empty
     *
     * @return the 'empty' view
     */
    var emptyView: View? = null
        private set

    // main layout of the view
    private var mOuterLayout: LinearLayout? = null

    // flag used when either the loadmore, swipe layout loading or full loading screen is displayed
    private var isLoading: Boolean = false

    // flag used to display or not the loadmore progress
    private var shouldLoadMore: Boolean = false

    // recycler view listener
    private lateinit var mInternalOnScrollListener: RecyclerView.OnScrollListener

    // List of all external scroll listerners
    private var mExternalOnScrollListeners: MutableList<RecyclerView.OnScrollListener> = ArrayList()

    // On More Listener
    private var mOnMoreListener: OnMoreListener? = null

    // Flag for disabling the loading view while the list is empty
    private var emptyLoadingViewEnabled = true

    // Generic adapter
    private lateinit var mAdapter: MultiGenericAdapter

    // Layouts
    var loadMoreLayout: Int = 0
    var loadingLayout: Int = 0
        set(layout) {
            field = layout
            mLoadingViewStub!!.layoutResource = layout
            loadingView = mLoadingViewStub!!.inflate()
        }
    var emptyLayout: Int = 0
        set(layout) {
            field = layout
            mEmptyViewStub!!.layoutResource = layout
            emptyView = mEmptyViewStub!!.inflate()

        }


    /**
     * Enable or disable the animation on new list entry
     *
     * @param animate true to enable layout animation, false to disable
     */
    fun setAnimateLayoutChange(animate: Boolean) {
        mOuterLayout?.layoutTransition = if (animate) LayoutTransition() else null
    }

    /**
     * Simple Constructor
     *
     * @param context the current context
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor with attributeSet
     *
     * @param context the current context
     * @param attrs   attribute set to customize the RecyclerView
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    /**
     * Constructor with attributeSet and style
     *
     * @param context  the current context
     * @param attrs    attribute set to customize the RecyclerView
     * @param defStyle style of the RecyclerView
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setup()
    }

    /**
     * Initialize the view
     */
    private fun setup() {
        //bypass for Layout display in UIBuilder
        if (isInEditMode) {
            return
        }
        mExternalOnScrollListeners = ArrayList()

        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.layout_smart_recycler_view, this)
        recyclerView = view.smart_list_recycler
        mEmptyViewStub = view.smart_list_empty_stub
        mLoadingViewStub = view.smart_list_loading_stub
        swipeLayout = view.smart_list_swipe_layout

        if (recyclerView != null) {
            mInternalOnScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - itemLeftMoreToLoad
                                && !isLoading && shouldLoadMore) {

                            isLoading = true
                            if (mOnMoreListener != null) {
                                mOnMoreListener!!.onMoreAsked(this@SmartRecyclerView.recyclerView!!.adapter!!.itemCount, itemLeftMoreToLoad, layoutManager.findLastVisibleItemPosition())
                            }
                        }
                    }

                    for (listener in mExternalOnScrollListeners)
                        listener.onScrolled(recyclerView, dx, dy)

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    for (listener in mExternalOnScrollListeners)
                        listener.onScrollStateChanged(recyclerView, newState)
                }
            }
            recyclerView!!.addOnScrollListener(mInternalOnScrollListener)

        }

        //by default, empty screen is hidden and loading screen is displayed
        if (emptyView != null)
            emptyView!!.visibility = View.GONE
        if (loadingView != null)
            loadingView!!.visibility = if (emptyLoadingViewEnabled) View.VISIBLE else View.GONE

    }

    /**
     * Set inner RecyclerView' LayoutManager
     *
     * @param layoutManager inner RecyclerView' LayoutManager
     */
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        if (recyclerView != null)
            recyclerView!!.layoutManager = layoutManager
    }

    /**
     * Set inner RecyclerView' adapter
     *
     * @param adapter inner RecyclerView' adapter
     */
    fun setAdapter(adapter: MultiGenericAdapter) {
        mAdapter = adapter
        if (recyclerView != null) {
            recyclerView!!.adapter = mAdapter

            this.updateAccessoryViews()

            mAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    updateAccessoryViews()
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    updateAccessoryViews()
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    updateAccessoryViews()
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                    updateAccessoryViews()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    super.onItemRangeChanged(positionStart, itemCount, payload)
                    updateAccessoryViews()
                }
            })
        }

    }

    /**
     * Update the view to display loading if needed
     */
    private fun updateAccessoryViews() {
        // hiding the loading view first
        loadingView?.visibility = View.GONE

        //flag indicating that the data loading is complete
        isLoading = false

        //hidding swipe to refresh too
        swipeLayout?.isRefreshing = false

        if (mAdapter.isEmpty) {
            emptyView?.visibility = View.VISIBLE
        } else {
            emptyView?.visibility = View.GONE
            //if there is more item to load, adding a placeholder cell at the end to display the load more
            if (shouldLoadMore) {
                //adding the viewHolder (this is safe to add without prior check, as the adapter is smart enought to not add it twice)
                if (loadMoreLayout > -1) {
                    mAdapter.addViewHolderType(PlaceHolderCell::class.java, PlaceHolderViewHolder::class.java, loadMoreLayout)
                }
                //removing and adding again the placeholder view to ensure it's always on the last positions
                mAdapter.deletePlaceholder()
                mAdapter.items.add(placeHolderCell)
                mAdapter.notifyDataSetChanged()

            } else {
                mAdapter.deletePlaceholder()
            }
        }
    }

    /**
     * Set the RecyclerView' SwipeRefreshListener
     *
     * @param listener the RecyclerView' SwipeRefreshListener
     */
    fun setRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener) {
        if (swipeLayout != null) {
            swipeLayout!!.isEnabled = true
            swipeLayout!!.setOnRefreshListener(listener)
        }
    }

    /**
     * set the OnMore Listener
     *
     * @param onMoreListener the LoadMore Listener
     * @param max            count of items left before firing the LoadMore
     */
    fun setOnMoreListener(onMoreListener: OnMoreListener, max: Int) {
        mOnMoreListener = onMoreListener
        itemLeftMoreToLoad = max
        shouldLoadMore = true
    }

    /**
     * Set the recyclerView' OnTouchListener
     *
     * @param listener OnTouchListener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: View.OnTouchListener) {
        if (recyclerView != null)
            recyclerView!!.setOnTouchListener(listener)
    }

    /**
     * Display the pull-to-refresh of the SwipeLayout
     *
     * @param display true to display the pull-to-refresh, false otherwise
     */
    fun enableSwipeToRefresh(display: Boolean) {
        if (swipeLayout != null)
            swipeLayout!!.isEnabled = display
    }


    /**
     * Tell the recyclerView that new item can be loaded after the end of the current list (load more)
     *
     * @param shouldLoadMore true to enable the LoadMore process, false otherwise
     */
    fun enableLoadMore(shouldLoadMore: Boolean) {
        this.shouldLoadMore = shouldLoadMore
        //updating accessory view if the recycler view isn't waiting for new data.
        // Otherwise, this could display an empty view between the time loadmore is changed
        // and new data are effectivelly added.
        if (!isLoading)
            updateAccessoryViews()
    }

    /**
     * Tell the recyclerView if the empty view should be displayed when there is no items.
     *
     * @param enableEmpty true to display the empty view, false otherwise
     */
    fun enableEmptyLoadingView(enableEmpty: Boolean) {
        emptyLoadingViewEnabled = enableEmpty
        loadingView?.visibility = View.GONE
    }

    /**
     * Display the loading placeholder ontop of the list
     */
    fun displayLoadingView() {
        //updating internal loading flag
        isLoading = true

        if (mAdapter.itemCount > 0) {
            if (swipeLayout != null && !swipeLayout!!.isRefreshing) {
                // on affiche le PTR
                val typedValue = TypedValue()
                context.theme.resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typedValue, true)
                swipeLayout?.setProgressViewOffset(false, 0, resources.getDimensionPixelSize(typedValue.resourceId))
                swipeLayout?.isRefreshing = true
            }
        } else {
            this.emptyView?.visibility = View.GONE
            this.loadingView?.visibility = View.VISIBLE
        }
    }


    /**
     * Add a custom cell divider to the recycler view
     *
     * @param dividerItemDecoration custom cell divider
     */
    fun addItemDecoration(dividerItemDecoration: RecyclerView.ItemDecoration) {
        if (recyclerView != null)
            recyclerView!!.addItemDecoration(dividerItemDecoration)
    }

    /**
     * Change view background color
     *
     * @param color view background color
     */
    override fun setBackgroundColor(color: Int) {
        if (recyclerView != null)
            recyclerView!!.setBackgroundColor(color)
    }

    /**
     * Scroll the recycler view to its top
     */
    fun scrollToTop() {
        if (!recyclerView!!.isAnimating && !recyclerView!!.isComputingLayout)
            recyclerView!!.scrollToPosition(0)
    }

    /**
     * Smooth scroll the recycler view to its top
     */
    fun smoothScrollToTop() {
        if (!recyclerView!!.isAnimating && !recyclerView!!.isComputingLayout)
            recyclerView!!.smoothScrollToPosition(0)
    }

    /**
     * @return the scroll listeners of the recyclerView
     */
    fun getExternalOnScrollListeners(): List<RecyclerView.OnScrollListener> {
        return mExternalOnScrollListeners
    }

    /**
     * Add a scroll listener to the recyclerView
     *
     * @param externalOnScrollListener
     */
    fun addExternalOnScrollListener(externalOnScrollListener: RecyclerView.OnScrollListener) {
        this.mExternalOnScrollListeners.add(externalOnScrollListener)
    }

    /**
     * Clear all scroll listeners of the recycler view
     */
    fun clearExternalOnScrollListeners() {
        mExternalOnScrollListeners.clear()
    }

    /**
     * Scroll to the bottom of the list
     */
    fun scrollToBottom() {
        if (recyclerView != null && mAdapter.itemCount > 0)
            recyclerView!!.scrollToPosition(mAdapter.itemCount - 1)
    }

    /**
     * Scroll smoothly to the bottom of the list
     */
    fun smoothScrollToBottom() {
        if (recyclerView != null && mAdapter.itemCount > 0)
            recyclerView!!.smoothScrollToPosition(mAdapter.itemCount - 1)
    }

    /**
     * Scroll without animation to an item position in the RecyclerView
     *
     * @param position position to scroll to
     */
    fun scrollTo(position: Int) {
        if (recyclerView != null)
            this.recyclerView!!.scrollToPosition(position)
    }

    /**
     * Scroll smoothly to an item position in the RecyclerView
     *
     * @param position position to scroll to
     */
    fun smoothScrollTo(position: Int) {
        if (recyclerView != null)
            this.recyclerView!!.smoothScrollToPosition(position)
    }

}
