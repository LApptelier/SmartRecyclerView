package com.lapptelier.test

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.lapptelier.smartrecyclerview.*
import com.lapptelier.test.databinding.ActivityTestBinding

/**
 * @author L'Apptelier SARL
 * @date 14/09/2017
 */
internal class TestActivity : AppCompatActivity(), ViewHolderInteractionListener, OnMoreListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var adapter: TestAdapter
    private lateinit var binding: ActivityTestBinding

    private val elements = listOf(
        "test 1",
        "test 2",
        "test 3",
        "test 4",
        "test 5",
        "test 6",
        "test 7",
        "test 8",
        "test 9",
        "test 10",
        "test 11",
        "test 12",
        "test 13",
        "test 14",
        "test 15",
        "test 16"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configuration de la liste
        binding.testSmartRecyclerView.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )

        // Configuration de l'adapter
        adapter = TestAdapter(this)
        binding.testSmartRecyclerView.setAdapter(adapter, true)
        binding.testSmartRecyclerView.addItemDecoration(
            DrawableDividerItemDecoration(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.divider
                ), null, true
            )
        )
        binding.testSmartRecyclerView.emptyLayout = R.layout.empty
        binding.testSmartRecyclerView.loadingLayout = R.layout.loading
        binding.testSmartRecyclerView.setOnMoreListener(this, 2)
        binding.testSmartRecyclerView.setRefreshListener(this)

        binding.testSmartRecyclerView.displayLoadingView()

        adapter.hasMore = true
        adapter.addAll(elements)

    }


    // OnMoreListener
    override fun onMoreAsked(
        overallItemsCount: Int,
        itemsBeforeMore: Int,
        maxLastVisiblePosition: Int
    ) {
        Log.d("test", "MORE MORE MORE !")

        Handler().postDelayed({
            adapter.hasMore = true
        }, 500)

        Handler().postDelayed({
            adapter.appendAll(elements)
        }, 1000)

    }

    override fun onRefresh() {
        Log.d("test", "FRESH-FRESH-FRESH !")
        Handler().postDelayed({
            adapter.hasMore = true
        }, 1000)

        Handler().postDelayed({
            adapter.addAll(elements)
        }, 2000)

    }


    override fun onItemAction(
        item: Any,
        viewId: Int,
        viewHolderInteraction: ViewHolderInteraction
    ) {
        if (item is String)
            when (viewHolderInteraction) {
                ViewHolderInteraction.LONG_TAP -> adapter.removeAt(adapter.getObjectIndex(item))
                else -> Snackbar.make(
                    binding.root,
                    "Tapped :$item",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }
}
