package com.lapptelier.test

import android.view.View
import android.widget.TextView
import com.lapptelier.smartrecyclerview.SmartViewHolder
import com.lapptelier.smartrecyclerview.ViewHolderInteractionListener

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
internal class TestViewHolder(view: View) : SmartViewHolder<String>(view) {
    var textView: TextView = view.findViewById(R.id.test_text)

    private var cellText: String? = null

    override fun setItem(item: String, listener: ViewHolderInteractionListener) {
        cellText = item
        textView.text = item

        textView.setOnClickListener { listener.onItemClicked(item) }
        textView.isLongClickable = true
    }
}
