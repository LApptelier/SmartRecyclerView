package com.lapptelier.smartrecyclerview

/**
 * com.msd.msdetmoi.common.util.new_smart_recycler_view.SmartBindingRecyclerView
 * <p/>
 * Generic implementation of a {@see RecyclerView} intagrated in {@SwipeRefreshLayout} layout.
 * Feature placeholder for empty list and loading progress bar both during reloading and loading more items.
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 02/09/2020
 */

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

open class SmartRecyclerView : LinearLayout {

    // count of item to display before firing the loadmore action
    private var itemLeftMoreToLoad = 10

    // actual recycler view
    var recyclerView: RecyclerView? = null

    // swipe layout
    var swipeLayout: SwipeRefreshLayout? = null

    // placeholder on the loading of the list
    var mLoadingViewStub: ViewStub? = null
    var mEmptyViewStub: ViewStub? = null

    /**
     * Return the inflated view layout placed when the list is loading elements
     *
     * @return the 'loading' view
     */
    var loadingView: View? = null

    /**
     * Return the inflated view layout placed when the list is empty
     *
     * @return the 'empty' view
     */
    var emptyView: View? = null

    // main layout of the view
    private var mOuterLayout: LinearLayout? = null

    // flag used when either the loadmore, swipe layout loading or full loading screen is displayed
    private var isLoading: Boolean = false

    // recycler view listener
    private lateinit var mInternalOnScrollListener: RecyclerView.OnScrollListener

    // List of all external scroll listerners
    private var mExternalOnScrollListeners: MutableList<RecyclerView.OnScrollListener> = ArrayList()

    // On More Listener
    private var mOnMoreListener: OnMoreListener? = null

    // Flag for disabling the loading view while the list is empty
    private var emptyLoadingViewEnabled = true

    // Generic adapter
    private var mAdapter: GenericAdapter? = null


    var loadingLayout: Int = 0
        set(layout) {
            field = layout
            mLoadingViewStub!!.layoutResource = layout
            loadingView = mLoadingViewStub!!.inflate()
            emptyLoadingViewEnabled = true
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
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
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
        recyclerView = view.findViewById(R.id.smart_list_recycler)
        mEmptyViewStub = view.findViewById(R.id.smart_list_empty_stub)
        mLoadingViewStub = view.findViewById(R.id.smart_list_loading_stub)
        swipeLayout = view.findViewById(R.id.smart_list_swipe_layout)

        if (recyclerView != null) {
            mInternalOnScrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                        if (layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - itemLeftMoreToLoad
                            && !isLoading && mAdapter != null && mAdapter!!.hasMore
                        ) {

                            isLoading = true
                            if (mOnMoreListener != null) {
                                mOnMoreListener!!.onMoreAsked(
                                    this@SmartRecyclerView.recyclerView!!.adapter!!.itemCount,
                                    itemLeftMoreToLoad,
                                    layoutManager.findLastVisibleItemPosition()
                                )
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
    fun setAdapter(adapter: GenericAdapter) {
        setAdapter(adapter, true)
    }

    /**
     * Set inner RecyclerView' adapter
     *
     * @param adapter inner RecyclerView' adapter
     * @param dismissLoadingViewAutomatically true to handle loading view automatically at every adapter changes
     */
    fun setAdapter(adapter: GenericAdapter, dismissLoadingViewAutomatically: Boolean) {
        mAdapter = adapter

        if (recyclerView != null) {
            recyclerView!!.adapter = mAdapter

            if (dismissLoadingViewAutomatically) {
                this.dismissLoadingView()

                mAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        super.onItemRangeChanged(positionStart, itemCount)
                        dismissLoadingView()
                    }

                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        dismissLoadingView()
                    }

                    override fun onChanged() {
                        super.onChanged()
                        dismissLoadingView()
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        super.onItemRangeRemoved(positionStart, itemCount)
                        dismissLoadingView()
                    }

                    override fun onItemRangeMoved(
                        fromPosition: Int,
                        toPosition: Int,
                        itemCount: Int
                    ) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                        dismissLoadingView()
                    }

                    override fun onItemRangeChanged(
                        positionStart: Int,
                        itemCount: Int,
                        payload: Any?
                    ) {
                        super.onItemRangeChanged(positionStart, itemCount, payload)
                        dismissLoadingView()
                    }
                })
            }
        }

    }

    /**
     * Update the view to display loading if needed
     */
    fun dismissLoadingView() {

        //flag indicating that the data loading is complete
        isLoading = false

        // hiding the loading view first
        loadingView?.visibility = View.GONE

        //hidding swipe to refresh too
        swipeLayout?.isRefreshing = false

        if (mAdapter == null || mAdapter!!.isEmpty) {
            emptyView?.visibility = View.VISIBLE
        } else {
            emptyView?.visibility = View.GONE
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
    }

    /**
     * Set the recyclerView' OnTouchListener
     *
     * @param listener OnTouchListener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun setOnTouchListener(listener: View.OnTouchListener) {
        recyclerView?.setOnTouchListener(listener)
    }

    /**
     * Display the pull-to-refresh of the SwipeLayout
     *
     * @param display true to display the pull-to-refresh, false otherwise
     */
    fun enableSwipeToRefresh(display: Boolean) {
        swipeLayout?.let {
            it.isEnabled = display
        }
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

        if (mAdapter != null && mAdapter!!.itemCount > 0) {
            if (swipeLayout != null && !swipeLayout!!.isRefreshing) {
                // on affiche le PTR
                val typedValue = TypedValue()
                context.theme.resolveAttribute(
                    androidx.appcompat.R.attr.actionBarSize,
                    typedValue,
                    true
                )
                swipeLayout?.setProgressViewOffset(
                    false,
                    0,
                    resources.getDimensionPixelSize(typedValue.resourceId)
                )
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
        if (recyclerView != null && mAdapter != null && mAdapter!!.itemCount > 0)
            recyclerView!!.scrollToPosition(mAdapter!!.itemCount - 1)
    }

    /**
     * Scroll smoothly to the bottom of the list
     */
    fun smoothScrollToBottom() {
        if (recyclerView != null && mAdapter != null && mAdapter!!.itemCount > 0)
            recyclerView!!.smoothScrollToPosition(mAdapter!!.itemCount - 1)
    }

    /**
     * Scroll without animation to an item position in the RecyclerView
     *
     * @param position position to scroll to
     */
    fun scrollTo(position: Int) {
        this.recyclerView?.scrollToPosition(position)
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
