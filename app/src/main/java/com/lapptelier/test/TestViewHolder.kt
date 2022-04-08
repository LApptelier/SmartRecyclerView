package com.lapptelier.test

import androidx.recyclerview.widget.RecyclerView
import com.lapptelier.smartrecyclerview.SmartBindingViewHolder
import com.lapptelier.smartrecyclerview.ViewHolderInteraction
import com.lapptelier.smartrecyclerview.ViewHolderInteractionListener
import com.lapptelier.test.databinding.CellTestBinding

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
internal class TestViewHolder(private val binding: CellTestBinding) :
    SmartBindingViewHolder<String?>(binding.root) {

    override fun setItem(
        item: String?,
        listener: ViewHolderInteractionListener,
        recyclerViewPool: RecyclerView.RecycledViewPool?
    ) {
        binding.text = item
        item?.let {
            binding.root.setOnClickListener {
                listener.onItemAction(
                    item,
                    binding.root.id,
                    ViewHolderInteraction.TAP
                )
            }
            binding.root.setOnLongClickListener {
                listener.onItemAction(
                    item,
                    binding.root.id,
                    ViewHolderInteraction.LONG_TAP
                )
                false
            }

        }
        binding.executePendingBindings()
    }
}