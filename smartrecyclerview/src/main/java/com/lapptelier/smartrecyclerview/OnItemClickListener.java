package com.lapptelier.smartrecyclerview;

import android.view.View;

/**
 * com.lapptelier.smartrecyclerview.OnItemClickListener
 * <p/>
 * This interface allows the implementer to receive click event on a given {@see SmartRecyclerView}
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
public interface OnItemClickListener {
    public void onItemClick(View view, int position);
}
