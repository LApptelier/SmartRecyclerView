package com.lapptelier.smartrecyclerview

/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Item decoration which draws item divider between each items.
 */
class DrawableDividerItemDecoration
/**
 * Constructor.
 *
 * @param horizontalDrawable horizontal divider drawable
 * @param verticalDrawable   vertical divider drawable
 * @param overlap           whether the divider is drawn overlapped on bottom (or right) of the item.
 */
(private val horizontalDrawable: Drawable?, private val verticalDrawable: Drawable?, private val overlap: Boolean) : RecyclerView.ItemDecoration() {
    private val mHorizontalDividerHeight: Int = horizontalDrawable?.intrinsicHeight ?: 0
    private val mVerticalDividerWidth: Int = verticalDrawable?.intrinsicWidth ?: 0

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount


        if (childCount == 0) {
            return
        }

        val xPositionThreshold = if (overlap) 1.0f else mVerticalDividerWidth + 1.0f // [px]
        val yPositionThreshold = if (overlap) 1.0f else mHorizontalDividerHeight + 1.0f // [px]
        val zPositionThreshold = 1.0f // [px]

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val nextChild = parent.getChildAt(i + 1)

            if (child.visibility != View.VISIBLE || nextChild.visibility != View.VISIBLE) {
                continue
            }

            // check if the next item is placed at the bottom or right
            val childBottom = child.bottom + child.translationY
            val nextChildTop = nextChild.top + child.translationY
            val childRight = child.right + child.translationX
            val nextChildLeft = nextChild.left + child.translationX

            if (!(mHorizontalDividerHeight != 0 && Math.abs(nextChildTop - childBottom) < yPositionThreshold || mVerticalDividerWidth != 0 && Math.abs(nextChildLeft - childRight) < xPositionThreshold)) {
                continue
            }

            // check if the next item is placed on the same plane
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val childZ = child.translationZ + child.elevation
                val nextChildZ = child.translationZ + child.elevation
                if (Math.abs(nextChildZ - childZ) >= zPositionThreshold) {
                    continue
                }
            }


            val childAlpha = child.alpha
            val nextChildAlpha = nextChild.alpha

            val tx = (child.translationX + 0.5f).toInt()
            val ty = (child.translationY + 0.5f).toInt()

            if (mHorizontalDividerHeight != 0) {
                val left = child.left
                val right = child.right
                val top = child.bottom - if (overlap) mHorizontalDividerHeight else 0
                val bottom = top + mHorizontalDividerHeight

                horizontalDrawable!!.alpha = (0.5f * 255 * (childAlpha + nextChildAlpha) + 0.5f).toInt()
                horizontalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty)
                horizontalDrawable.draw(c)
            }

            if (mVerticalDividerWidth != 0) {
                val left = child.right - if (overlap) mVerticalDividerWidth else 0
                val right = left + mVerticalDividerWidth
                val top = child.top
                val bottom = child.bottom

                verticalDrawable!!.alpha = (0.5f * 255 * (childAlpha + nextChildAlpha) + 0.5f).toInt()
                verticalDrawable.setBounds(left + tx, top + ty, right + tx, bottom + ty)
                verticalDrawable.draw(c)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (overlap) {
            outRect.set(0, 0, 0, 0)
        } else {
            outRect.set(0, 0, mVerticalDividerWidth, mHorizontalDividerHeight)
        }
    }
}