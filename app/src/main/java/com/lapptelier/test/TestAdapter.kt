package com.lapptelier.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lapptelier.smartrecyclerview.GenericAdapter
import com.lapptelier.smartrecyclerview.PlaceHolderCell
import com.lapptelier.smartrecyclerview.PlaceHolderVH
import com.lapptelier.smartrecyclerview.ViewHolderInteractionListener
import com.lapptelier.test.databinding.CellTestBinding
import com.lapptelier.test.databinding.LoadmoreBinding

/**
 * com.lapptelier.test.TestAdapter
 * <p/>
 *
 * <p/>
 *
 * @author L'Apptelier SARL
 * @date 08/04/2022
 */
class TestAdapter(
    listener: ViewHolderInteractionListener
) : GenericAdapter(listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.cell_test -> {
                val binding = CellTestBinding.inflate(inflater, parent, false)
                TestViewHolder(binding)
            }
            R.layout.loadmore ->{
                val binding = LoadmoreBinding.inflate(inflater, parent, false)
                PlaceHolderVH(binding)
            }
            else -> {
                throw RuntimeException("No view holder for view type $viewType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> R.layout.cell_test
            is PlaceHolderCell -> R.layout.loadmore
            else -> {
                -1
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            when (it) {
                is String -> (holder as TestViewHolder).setItem(
                    it,
                    listener,
                    RecyclerView.RecycledViewPool()
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}