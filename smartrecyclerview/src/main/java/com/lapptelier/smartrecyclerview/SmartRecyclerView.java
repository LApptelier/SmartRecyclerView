package com.lapptelier.smartrecyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.socks.library.KLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.SmartRecyclerView
 * <p/>
 * Generic implementation of a {@see RecyclerView} intagrated in {@SwipeRefreshLayout} layout.
 * <p/>
 * Feature placeholder for empty list and loading progress bar both during reloading and loading more items.
 * <p/>
 * Sub class of {@link LinearLayout}.
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public class SmartRecyclerView extends LinearLayout {

    protected int ITEM_LEFT_TO_LOAD_MORE = 10; // count of item to display before firing the loadmore action

    @BindView(R2.id.smart_list_recycler)
    protected RecyclerView mRecyclerView;
    @BindView(R2.id.smart_list_swipe_layout)
    protected SwipeRefreshLayout mSwipeLayout;
    @BindView(R2.id.smart_list_loading_stub)
    protected ViewStub mLoadingViewStub;
    @BindView(R2.id.smart_list_empty_stub)
    protected ViewStub mEmptyViewStub;

    protected View mLoadingView;
    protected View mEmptyView;

    protected boolean isLoadingMore; // flag used when the loadmore progress is displayed
    protected boolean shouldLoadMore; // flag used to display or not the loadmore progress

    private static PlaceHolderCell placeHolderCell = new PlaceHolderCell(); // placeholder cell used to display load more progress

    // recycler view listener
    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    private RecyclerView.OnScrollListener mSwipeDismissScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;
    protected OnMoreListener mOnMoreListener;

    // generic adapter
    protected SmartAdapter mAdapter;

    // id of the load more layout
    protected int loadMoreLayout;

    /**
     * Simple Constructor
     *
     * @param context the current context
     */
    public SmartRecyclerView(Context context) {
        super(context);
    }

    /**
     * Constructor with attributeSet
     *
     * @param context the current context
     * @param attrs   attribute set to customize the RecyclerView
     */
    public SmartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor with attributeSet and style
     *
     * @param context  the current context
     * @param attrs    attribute set to customize the RecyclerView
     * @param defStyle style of the RecyclerView
     */
    public SmartRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize the view
     */
    private void init() {
        //bypass for Layout display in UIBuilder
        if (isInEditMode()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.layout_smart_recycler_view, this);
        ButterKnife.bind(this, view);

        initRecyclerView();

    }

    /**
     * Initialize the inner RecyclerView
     */
    protected void initRecyclerView() {
        if (mRecyclerView != null) {
            mInternalOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (recyclerView.getLayoutManager().getClass().equals(LinearLayoutManager.class)) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();

                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                        if (((totalItemCount - lastVisibleItemPosition) == ITEM_LEFT_TO_LOAD_MORE ||
                                (totalItemCount - lastVisibleItemPosition) == 0 && totalItemCount > visibleItemCount)
                                && !isLoadingMore && shouldLoadMore) {

                            isLoadingMore = true;
                            if (mOnMoreListener != null) {
                                mOnMoreListener.onMoreAsked(mRecyclerView.getAdapter().getItemCount(), ITEM_LEFT_TO_LOAD_MORE, lastVisibleItemPosition);
                            }
                        }
                    }

                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);
                    if (mSwipeDismissScrollListener != null)
                        mSwipeDismissScrollListener.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                    if (mSwipeDismissScrollListener != null)
                        mSwipeDismissScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            };
            mRecyclerView.addOnScrollListener(mInternalOnScrollListener);

        }

        //by default, empty screen is hidden and loading screen is displayed
        if (mEmptyView != null)
            mEmptyView.setVisibility(View.GONE);
        if (mLoadingView != null)
            mLoadingView.setVisibility(View.VISIBLE);

    }

    /**
     * Set inner RecyclerView' LayoutManager
     *
     * @param layoutManager inner RecyclerView' LayoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecyclerView != null)
            mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Set inner RecyclerView' adapter
     *
     * @param adapter inner RecyclerView' adapter
     */
    public void setAdapter(final SmartAdapter adapter) {
        mAdapter = adapter;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);

            this.updateAccessoryViews();

            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    updateAccessoryViews();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    updateAccessoryViews();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    updateAccessoryViews();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    updateAccessoryViews();
                }

                @Override
                public void onChanged() {
                    super.onChanged();
                    updateAccessoryViews();
                }

            });
        }

    }

//    /**
//     * Set RecyclerView' SwipeToDismiss Listener
//     *
//     * @param listener inner RecyclerView' SwipeToDismiss Listener
//     */
//    public void setupSwipeToDismiss(final SwipeDismissRecyclerViewTouchListener.DismissCallbacks listener) {
//        SwipeDismissRecyclerViewTouchListener touchListener =
//                new SwipeDismissRecyclerViewTouchListener(mRecyclerView, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
//                    @Override
//                    public boolean canDismiss(int position) {
//                        return listener.canDismiss(position);
//                    }
//
//                    @Override
//                    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
//                        listener.onDismiss(recyclerView, reverseSortedPositions);
//                    }
//                });
//        mSwipeDismissScrollListener = touchListener.makeScrollListener();
//        if (mRecyclerView != null)
//            mRecyclerView.setOnTouchListener(touchListener);
//    }

    /**
     * Update the view to display loading if needed
     */
    private void updateAccessoryViews() {
        // hiddig the loading view first
        if (mLoadingView != null)
            mLoadingView.setVisibility(View.GONE);

        //flag indicating that the data loading is complete
        isLoadingMore = false;

        //hidding swipe to refresh too
        mSwipeLayout.setRefreshing(false);

        if (mAdapter.isEmpty()) {
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.VISIBLE);
        } else {
            if (mEmptyView != null)
                mEmptyView.setVisibility(View.GONE);
            //if there is more item to load, adding a placeholder cell at the end to display the load more
            if (shouldLoadMore) {
                if (!mAdapter.contains(placeHolderCell) && loadMoreLayout > -1) {
                    //adding directly to the adapter list to avoid firing callbacks
                    mAdapter.items.add(placeHolderCell);

                    //adding the viewHolder (this is safe to add without prior check, as the adapter is smart enought to not add it twice)
                    if (mAdapter.getClass().equals(MultiGenericAdapter.class))
                        ((MultiGenericAdapter) mAdapter).addViewHolderType(PlaceHolderCell.class, PlaceHolderViewHolder.class, loadMoreLayout);
                }
            }
        }
    }

    /**
     * Set the RecyclerView' SwipeRefreshListener
     *
     * @param listener the RecyclerView' SwipeRefreshListener
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (mSwipeLayout != null) {
            mSwipeLayout.setEnabled(true);
            mSwipeLayout.setOnRefreshListener(listener);
        }
    }

    /**
     * Add a the RecyclerView' OnItemTouchListener
     *
     * @param listener a RecyclerView' OnItemTouchListener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        if (mRecyclerView != null)
            mRecyclerView.addOnItemTouchListener(listener);
    }

    /**
     * set the LoadMore Listener
     *
     * @param onMoreListener the LoadMore Listener
     * @param max            count of items left before firing the LoadMore
     */
    public void setupMoreListener(OnMoreListener onMoreListener, int max) {
        mOnMoreListener = onMoreListener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    /**
     * Set the recyclerView' OnTouchListener
     *
     * @param listener OnTouchListener
     */
    public void setOnTouchListener(OnTouchListener listener) {
        if (mRecyclerView != null)
            mRecyclerView.setOnTouchListener(listener);
    }

    /**
     * Display the pull-to-refresh of the SwipeLayout
     *
     * @param display true to display the pull-to-refresh, false otherwise
     */
    public void displaySwipeToRefresh(boolean display) {
        if (mSwipeLayout != null)
            mSwipeLayout.setEnabled(display);
    }


    /**
     * Tell the recyclerView that new item can be loaded after the end of the current list (load more)
     *
     * @param shouldLoadMore true to enable the LoadMore process, false otherwise
     */
    public void shouldLoadMore(boolean shouldLoadMore) {
        this.shouldLoadMore = shouldLoadMore;
    }

    /**
     * Set the view displayed when no items are in the list. Should have a frame layout at its root.
     *
     * @param viewLayout the view layout to inflate
     */
    public void setEmptyLayout(int viewLayout) {
        mEmptyViewStub.setLayoutResource(viewLayout);
        mEmptyView = mEmptyViewStub.inflate();
    }

    /**
     * Set the view displayed during loadings. Should have a frame layout at its root.
     *
     * @param viewLayout the view layout to inflate
     */
    public void setLoadingLayout(int viewLayout) {
        mLoadingViewStub.setLayoutResource(viewLayout);
        mLoadingView = mLoadingViewStub.inflate();
    }

    /**
     * Display the loading placeholder ontop of the list
     */
    public void displayLoadingView() {
        //si on a un PTR de setté, on l'affiche, sinon on affiche le message de chargement
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            if (mSwipeLayout != null && !mSwipeLayout.isRefreshing()) {
                // on affiche le PTR
                TypedValue typed_value = new TypedValue();
                getContext().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
                mSwipeLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
                mSwipeLayout.setRefreshing(true);
            }
        } else {
            if (mLoadingView != null)
                this.mLoadingView.setVisibility(VISIBLE);
        }
    }

    /**
     * Clear all elements in the RecyclerView' Adapter list
     */
    public void clear() {
        if (mAdapter != null)
            mAdapter.clear();
    }

    /**
     * Add all given items in the RecyclerView' Adapter list
     *
     * @param items item to add
     */
    public void addAll(List<?> items) {
        if (mAdapter != null) {
            this.deletePlaceholder();
            mAdapter.addAll(items);
        }
    }

    /**
     * Delete all placeholders
     */
    private void deletePlaceholder() {
        if (mAdapter != null && mAdapter.contains(placeHolderCell)) {
            KLog.d("deletePlaceholder", "done");
            //on attaque directement la liste pr éviter les callbacks supplémentaires lors de l'ajout/suppression d'éléments
            mAdapter.items.remove(mAdapter.getItemCount() - 1);
        }
    }

    /**
     * Append an item to the RecyclerView' Adapter list
     *
     * @param item item to append
     */
    public void add(Object item) {
        if (mAdapter != null) {
            this.deletePlaceholder();
            mAdapter.add(item);
        }
    }

    /**
     * Insert a given item at a given position in the RecyclerView' Adapter list
     *
     * @param item     item to insert
     * @param position position to insert to
     */
    public void insertAt(int position, Object item) {
        if (mAdapter != null)
            mAdapter.insertAt(position, item);
    }

    /**
     * Delete an item from the RecyclerView' Adapter list
     *
     * @param position position of the item to delete
     */
    public void removeAt(int position) {
        if (mAdapter != null)
            mAdapter.removeAt(position);
    }

    /**
     * Replace an item at a given position by another given item of the RecyclerView' Adapter list
     *
     * @param position position of the item to replace
     * @param item     new item
     */
    public void replaceAt(int position, Object item) {
        if (mAdapter != null)
            mAdapter.replaceAt(position, item);
    }

    /**
     * Return true if the RecyclerView' Adapter list is empty
     *
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        if (mAdapter != null)
            return mAdapter.isEmpty();
        else
            return true;
    }

    /**
     * Return the RecyclerView' Adapter item at the given position
     *
     * @param position position of the item
     * @return the item at the given position, null otherwise
     */
    public Object getItemAt(int position) {
        if (mAdapter != null)
            return mAdapter.getItemAt(position);
        else
            return null;
    }

    /**
     * Return the position in the RecyclerView' Adapter list of the given item
     *
     * @param object item to search in the list
     * @return item's position if found, -1 otherwise
     */
    public int getObjectIndex(Object object) {
        if (mAdapter != null)
            return mAdapter.getObjectIndex(object);
        else
            return -1;
    }

    /**
     * Return the item's count of the RecyclerView' Adapter list
     *
     * @return the item's count of the list
     */
    public int getItemCount() {

        if (mAdapter != null)
            return mAdapter.getItemCount();
        else
            return -1;
    }

    /**
     * Scroll smoothly to an item position in the RecyclerView
     *
     * @param position position to scroll to
     */
    public void smoothScrollTo(int position) {
        if (mRecyclerView != null)
            this.mRecyclerView.smoothScrollToPosition(position);
    }

    /**
     * Tell the recycler view that its data has been changed
     */
    public void notifyDataSetChanged() {
        if (mAdapter != null)
            this.mAdapter.notifyDataSetChanged();
    }

    /**
     * Add a custom cell divider to the recycler view
     *
     * @param dividerItemDecoration custom cell divider
     */
    public void addItemDecoration(RecyclerView.ItemDecoration dividerItemDecoration) {
        if (mRecyclerView != null)
            mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    /**
     * Change view background color
     *
     * @param color view background color
     */
    @Override
    public void setBackgroundColor(int color) {
        if (mRecyclerView != null)
            mRecyclerView.setBackgroundColor(color);
    }


    /**
     * Set the color scheme of the loading and loadmore circular progress bar
     *
     * @param color1 color used on the circular progress bar
     * @param color2 color used on the circular progress bar
     * @param color3 color used on the circular progress bar
     * @param color4 color used on the circular progress bar
     */
    public void setProgressColorScheme(int color1, int color2, int color3, int color4) {
        if (mSwipeLayout != null)
            mSwipeLayout.setColorSchemeResources(color1, color2, color3, color4);
    }

    /**
     * Set the layout used to populate the loadmore placeholder cell
     *
     * @param loadMoreLayout the layout to inflate
     */
    public void setLoadMoreLayout(int loadMoreLayout) {
        this.loadMoreLayout = loadMoreLayout;
    }

    /**
     * Scroll the recycler view to its top
     */
    public void scroollToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }
}
