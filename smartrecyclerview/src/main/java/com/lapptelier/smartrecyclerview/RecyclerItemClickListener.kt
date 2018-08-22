package com.lapptelier.smartrecyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * com.lapptelier.smartrecyclerview.smart_recycler_view.RecyclerItemClickListener
 *
 *
 * Generic implementation of a {@see RecyclerView.OnItemTouchListener}.
 *
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
class RecyclerItemClickListener(context: Context, private val mListener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {

    private var mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }
    })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildPosition(childView))
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}
