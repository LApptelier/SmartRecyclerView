package com.lapptelier.smartrecyclerview

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
    private var mRecyclerView: RecyclerView? = null

    // swipe layout
    var swipeLayout: SwipeRefreshLayout? = null

    // placeholder on the loading of the list
    var mLoadingViewStub: ViewStub? = null
    var mEmptyViewStub: ViewStub? = null

    // placeholder cell used to display load more progress
    val placeHolderCell = PlaceHolderCell()

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

    // flag used when the loadmore progress is displayed
    private var isLoadingMore: Boolean = false

    // flag used to display or not the loadmore progress
    private var shouldLoadMore: Boolean = false

    // recycler view listener
    lateinit var mInternalOnScrollListener: RecyclerView.OnScrollListener

    // List of all external scroll listerners
    var mExternalOnScrollListeners: MutableList<RecyclerView.OnScrollListener> = ArrayList()

    // On More Listener
    var mOnMoreListener: OnMoreListener? = null

    // Generic adapter
    lateinit var mAdapter: MultiGenericAdapter

    // Id of the load more layout
    var loadMoreLayout: Int = 0

    // Flag for disabling the loading view while the list is empty
    var emptyLoadingViewEnabled = true

    /**
     * Return true if the RecyclerView' Adapter list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    val isEmpty: Boolean
        get() = mAdapter.isEmpty

    /**
     * Return the item's count of the RecyclerView' Adapter list
     *
     * @return the item's count of the list
     */
    val itemCount: Int
        get() = mAdapter.itemCount

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
        mRecyclerView = view.smart_list_recycler
        mEmptyViewStub = view.smart_list_empty_stub
        mLoadingViewStub = view.smart_list_loading_stub
        swipeLayout = view.smart_list_swipe_layout

        if (mRecyclerView != null) {
            mInternalOnScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - itemLeftMoreToLoad
                                && !isLoadingMore && shouldLoadMore) {

                            isLoadingMore = true
                            if (mOnMoreListener != null) {
                                mOnMoreListener!!.onMoreAsked(mRecyclerView!!.adapter.itemCount, itemLeftMoreToLoad, layoutManager.findLastVisibleItemPosition())
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
            mRecyclerView!!.addOnScrollListener(mInternalOnScrollListener)

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
        if (mRecyclerView != null)
            mRecyclerView!!.layoutManager = layoutManager
    }

    /**
     * Set inner RecyclerView' adapter
     *
     * @param adapter inner RecyclerView' adapter
     */
    fun setAdapter(adapter: MultiGenericAdapter) {
        mAdapter = adapter
        if (mRecyclerView != null) {
            mRecyclerView!!.adapter = mAdapter

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

                override fun onChanged() {
                    super.onChanged()
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
        // hiddig the loading view first
        if (loadingView != null)
            loadingView!!.visibility = View.GONE

        //flag indicating that the data loading is complete
        isLoadingMore = false

        //hidding swipe to refresh too
        if (swipeLayout != null)
            swipeLayout!!.isRefreshing = false

        if (mAdapter.isEmpty) {
            if (emptyView != null)
                emptyView!!.visibility = View.VISIBLE
        } else {
            if (emptyView != null)
                emptyView!!.visibility = View.GONE
            //if there is more item to load, adding a placeholder cell at the end to display the load more
            if (shouldLoadMore) {
                if (!mAdapter.contains(placeHolderCell) && loadMoreLayout > -1) {
                    //adding directly to the adapter list to avoid firing callbacks
                    mAdapter.items!!.add(placeHolderCell)

                    //adding the viewHolder (this is safe to add without prior check, as the adapter is smart enought to not add it twice)
                    mAdapter.addViewHolderType(PlaceHolderCell::class.java, PlaceHolderViewHolder::class.java, loadMoreLayout)
                }
            } else {
                if (mAdapter.contains(placeHolderCell)) {
                    mAdapter.removeAt(mAdapter.getObjectIndex(placeHolderCell))
                }
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
     * set the LoadMore Listener
     *
     * @param onMoreListener the LoadMore Listener
     * @param max            count of items left before firing the LoadMore
     */
    fun setupMoreListener(onMoreListener: OnMoreListener, max: Int) {
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
        if (mRecyclerView != null)
            mRecyclerView!!.setOnTouchListener(listener)
    }

    /**
     * Display the pull-to-refresh of the SwipeLayout
     *
     * @param display true to display the pull-to-refresh, false otherwise
     */
    fun displaySwipeToRefresh(display: Boolean) {
        if (swipeLayout != null)
            swipeLayout!!.isEnabled = display
    }


    /**
     * Tell the recyclerView that new item can be loaded after the end of the current list (load more)
     *
     * @param shouldLoadMore true to enable the LoadMore process, false otherwise
     */
    fun shouldLoadMore(shouldLoadMore: Boolean) {
        this.shouldLoadMore = shouldLoadMore
        updateAccessoryViews()
    }

    /**
     * Set the view displayed when no items are in the list. Should have a frame layout at its root.
     *
     * @param viewLayout the view layout to inflate
     */
    fun setEmptyLayout(viewLayout: Int) {
        mEmptyViewStub!!.layoutResource = viewLayout
        emptyView = mEmptyViewStub!!.inflate()
    }

    /**
     * Set the view displayed during loadings. Should have a frame layout at its root.
     *
     * @param viewLayout the view layout to inflate
     */
    fun setLoadingLayout(viewLayout: Int) {
        mLoadingViewStub!!.layoutResource = viewLayout
        loadingView = mLoadingViewStub!!.inflate()
    }

    /**
     * Display the loading placeholder ontop of the list
     */
    fun displayLoadingView() {
        if (mAdapter.itemCount > 0) {
            if (swipeLayout != null && !swipeLayout!!.isRefreshing) {
                // on affiche le PTR
                val typed_value = TypedValue()
                context.theme.resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true)
                swipeLayout!!.setProgressViewOffset(false, 0, resources.getDimensionPixelSize(typed_value.resourceId))
                swipeLayout!!.isRefreshing = true
            }
        } else {
            if (loadingView != null)
                this.loadingView!!.visibility = View.VISIBLE
        }
    }

    /**
     * Scroll smoothly to an item position in the RecyclerView
     *
     * @param position position to scroll to
     */
    fun smoothScrollTo(position: Int) {
        if (mRecyclerView != null)
            this.mRecyclerView!!.smoothScrollToPosition(position)
    }

    /**
     * Add a custom cell divider to the recycler view
     *
     * @param dividerItemDecoration custom cell divider
     */
    fun addItemDecoration(dividerItemDecoration: RecyclerView.ItemDecoration) {
        if (mRecyclerView != null)
            mRecyclerView!!.addItemDecoration(dividerItemDecoration)
    }

    /**
     * Change view background color
     *
     * @param color view background color
     */
    override fun setBackgroundColor(color: Int) {
        if (mRecyclerView != null)
            mRecyclerView!!.setBackgroundColor(color)
    }

    /**
     * Scroll the recycler view to its top
     */
    fun scrollToTop() {
        if (!mRecyclerView!!.isAnimating && !mRecyclerView!!.isComputingLayout)
            mRecyclerView!!.scrollToPosition(0)
    }

    /**
     * Smooth scroll the recycler view to its top
     */
    fun smoothScrollToTop() {
        if (!mRecyclerView!!.isAnimating && !mRecyclerView!!.isComputingLayout)
            mRecyclerView!!.smoothScrollToPosition(0)
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
        if (mRecyclerView != null && mAdapter.itemCount > 0)
            mRecyclerView!!.scrollToPosition(mAdapter.itemCount - 1)
    }


}
