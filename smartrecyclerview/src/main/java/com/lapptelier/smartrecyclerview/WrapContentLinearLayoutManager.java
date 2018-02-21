package com.lapptelier.smartrecyclerview;

/**
 * com.lapptelier.smartrecyclerview.wrapper
 * <p/>
 * Subclass of LinearLayoutManager. Correct a bug that can occurs with multiple ViewHolders
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 21/02/2018
 * @see LinearLayoutManager
 * @see RecyclerView
 * @see https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in#33822747
 * <p/>
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.socks.library.KLog;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {

    /**
     * Constructors
     */
    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //... constructor
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            KLog.w("Meet a IOOBE in RecyclerView");
        }
    }
}
