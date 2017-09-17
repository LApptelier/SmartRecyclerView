package com.lapptelier.smartrecyclerview;

/**
 * com.lapptelier.smartrecyclerview
 *
 * @author L'Apptelier SARL
 * @date 17/09/2017
 */
public interface OnMoreListener {
    /**
     * @param overallItemsCount
     * @param itemsBeforeMore
     * @param maxLastVisiblePosition for staggered grid this is max of all spans
     */
    void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition);
}
